package com.mj.homelibrary.ui

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.mj.homelibrary.HomeLibraryApplication
import com.mj.homelibrary.R
import com.mj.homelibrary.data.AppFontFamily
import com.mj.homelibrary.data.BackgroundTintLevel
import com.mj.homelibrary.data.BookSortCode
import com.mj.homelibrary.data.ColorSource
import com.mj.homelibrary.data.FabPlacement
import com.mj.homelibrary.data.FontScalePreference
import com.mj.homelibrary.data.LanguageCode
import com.mj.homelibrary.data.LibrarySettings
import com.mj.homelibrary.data.ReadStatusCode
import com.mj.homelibrary.data.ThemeColorIntensity
import com.mj.homelibrary.data.ThemePreference
import com.mj.homelibrary.data.entity.BookEntity
import com.mj.homelibrary.data.entity.BorrowerEntity
import com.mj.homelibrary.data.entity.LoanEntity
import com.mj.homelibrary.data.entity.LocationEntity
import com.mj.homelibrary.data.entity.QuoteEntity
import com.mj.homelibrary.data.normalizedIsbn
import com.mj.homelibrary.data.normalizedIsbn10OrNull
import com.mj.homelibrary.data.normalizedIsbn13OrNull
import com.mj.homelibrary.data.remote.BookMetadata
import com.mj.homelibrary.worker.BackupReminderWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.time.LocalDate
import java.time.ZoneId

class HomeLibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val homeLibraryApplication = application as HomeLibraryApplication
    private val repository = homeLibraryApplication.repository
    private val appearanceRepository = homeLibraryApplication.appearanceRepository
    private val librarySettingsRepository = homeLibraryApplication.librarySettingsRepository
    private val filters = MutableStateFlow(
        LibraryFilters(
            sort = librarySettingsRepository.settings.value.defaultSort,
            gridMode = librarySettingsRepository.settings.value.defaultGridMode,
        ),
    )
    private val transient = MutableStateFlow(TransientState())

    val appearanceSettings = appearanceRepository.settings
    val librarySettings = librarySettingsRepository.settings

    init {
        BackupReminderWorker.reschedule(WorkManager.getInstance(application), librarySettings.value.backupReminderDays)
    }

    private val catalogData = combine(
        repository.books,
        repository.locations,
        repository.loans,
    ) { books, locations, loans ->
        val activeLoans = loans.filter { it.actualReturnDateEpochMillis == null }
        val locationById = locations.associateBy { it.id }
        val activeLoanByBookId = activeLoans.associateBy { it.bookId }
        val allItems = books.map { book ->
            BookListItem(
                book = book,
                location = locationById[book.locationId],
                activeLoan = activeLoanByBookId[book.id],
            )
        }
        CatalogData(
            books = books,
            allItems = allItems,
            locations = locations,
            loans = loans,
            activeLoans = activeLoans,
            itemByBookId = allItems.associateBy { it.book.id },
            itemsByLocationId = allItems
                .mapNotNull { item -> item.book.locationId?.let { it to item } }
                .groupBy(keySelector = { it.first }, valueTransform = { it.second }),
            loansByBookId = loans.groupBy { it.bookId },
            bookByIsbn = buildMap {
                books.forEach { book ->
                    book.isbn10?.let { putIfAbsent(it, book) }
                    book.isbn13?.let { putIfAbsent(it, book) }
                }
            },
            searchKeyByBookId = books.associate { it.id to it.toSearchKey() },
            stats = LibraryStats.from(books, loans, activeLoans),
        )
    }

    private val persistentState = combine(catalogData, repository.borrowers, repository.allQuotes, filters) { catalog, borrowers, quotes, filters ->
        val normalizedQuery = filters.query.searchKey()
        val visibleItems = catalog.allItems.asSequence()
            .filter { item ->
                filters.matches(
                    item = item,
                    normalizedQuery = normalizedQuery,
                    searchKey = catalog.searchKeyByBookId[item.book.id].orEmpty(),
                )
            }
            .sortedWith(filters.sort.comparator())
            .toList()

        HomeLibraryUiState(
            allBooks = catalog.books,
            allItems = catalog.allItems,
            visibleBooks = visibleItems,
            locations = catalog.locations,
            loans = catalog.loans,
            activeLoans = catalog.activeLoans,
            borrowers = borrowers,
            quotes = quotes,
            quotesByBookId = quotes.groupBy { it.bookId },
            filters = filters,
            stats = catalog.stats,
            itemByBookId = catalog.itemByBookId,
            itemsByLocationId = catalog.itemsByLocationId,
            visibleItemsByLocationId = visibleItems
                .mapNotNull { item -> item.book.locationId?.let { it to item } }
                .groupBy(keySelector = { it.first }, valueTransform = { it.second }),
            loansByBookId = catalog.loansByBookId,
            bookByIsbn = catalog.bookByIsbn,
        )
    }

    val state: StateFlow<HomeLibraryUiState> = combine(persistentState, transient) { state, transient ->
        state.copy(transient = transient)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeLibraryUiState(),
    )

    fun setQuery(query: String) {
        filters.update { it.copy(query = query) }
    }

    fun setSort(sort: BookSortCode) {
        librarySettingsRepository.setDefaultSort(sort)
        filters.update { it.copy(sort = sort) }
    }

    fun setGridMode(enabled: Boolean) {
        librarySettingsRepository.setDefaultGridMode(enabled)
        filters.update { it.copy(gridMode = enabled) }
    }

    fun setLanguage(languageCode: String?) {
        filters.update { it.copy(languageCode = languageCode) }
    }

    fun setReadStatus(readStatusCode: String?) {
        filters.update { it.copy(readStatusCode = readStatusCode) }
    }

    fun setOnLoanOnly(enabled: Boolean) {
        filters.update { it.copy(onLoanOnly = enabled) }
    }

    fun setLocation(locationId: Long?) {
        filters.update { it.copy(locationId = locationId) }
    }

    fun setTag(tag: String?) {
        filters.update { it.copy(tag = tag) }
    }

    fun setMainGenreFilter(mainGenre: String?) {
        filters.update { it.copy(mainGenre = mainGenre) }
    }

    fun setSubGenreFilter(subGenre: String?) {
        filters.update { it.copy(subGenre = subGenre) }
    }

    fun clearFilters() {
        filters.update {
            it.copy(
                query = "",
                languageCode = null,
                readStatusCode = null,
                onLoanOnly = false,
                locationId = null,
                tag = null,
                mainGenre = null,
                subGenre = null,
            )
        }
    }

    fun setThemePreference(preference: ThemePreference) {
        appearanceRepository.setThemePreference(preference)
    }

    fun setColorSource(source: ColorSource) {
        appearanceRepository.setColorSource(source)
    }

    fun setThemeColorIntensity(intensity: ThemeColorIntensity) {
        appearanceRepository.setThemeColorIntensity(intensity)
    }

    fun setBackgroundTintLevel(level: BackgroundTintLevel) {
        appearanceRepository.setBackgroundTintLevel(level)
    }

    fun setAppFontFamily(fontFamily: AppFontFamily) {
        appearanceRepository.setAppFontFamily(fontFamily)
    }

    fun setFontScalePreference(preference: FontScalePreference) {
        appearanceRepository.setFontScalePreference(preference)
    }

    fun setContentFontScalePreference(preference: FontScalePreference) {
        appearanceRepository.setContentFontScalePreference(preference)
    }

    fun setFollowUiFontScale(enabled: Boolean) {
        appearanceRepository.setFollowUiFontScale(enabled)
    }

    fun setFabPlacement(placement: FabPlacement) {
        appearanceRepository.setFabPlacement(placement)
    }

    fun setLoanRemindersEnabled(enabled: Boolean) {
        librarySettingsRepository.setLoanRemindersEnabled(enabled)
    }

    fun setLoanReminderLeadDays(days: Int) {
        librarySettingsRepository.setLoanReminderLeadDays(days)
    }

    fun setBackupReminderDays(days: Int) {
        librarySettingsRepository.setBackupReminderDays(days)
        BackupReminderWorker.reschedule(WorkManager.getInstance(getApplication()), librarySettingsRepository.settings.value.backupReminderDays)
    }

    fun setReadingGoal(goal: Int) {
        librarySettingsRepository.setReadingGoal(goal)
    }

    fun setPrimaryLanguage(languageCode: String) {
        librarySettingsRepository.setPrimaryLanguage(languageCode)
    }

    fun toggleSelectionMode(enabled: Boolean = !transient.value.selectionMode) {
        transient.update {
            it.copy(
                selectionMode = enabled,
                selectedBookIds = if (enabled) it.selectedBookIds else emptySet()
            )
        }
    }

    fun toggleSelectBook(bookId: Long) {
        transient.update { state ->
            val updated = if (bookId in state.selectedBookIds) {
                state.selectedBookIds - bookId
            } else {
                state.selectedBookIds + bookId
            }
            state.copy(
                selectionMode = updated.isNotEmpty(),
                selectedBookIds = updated
            )
        }
    }

    fun selectAllVisible(bookIds: List<Long>) {
        transient.update {
            it.copy(
                selectionMode = true,
                selectedBookIds = bookIds.toSet()
            )
        }
    }

    fun clearSelection() {
        transient.update {
            it.copy(
                selectionMode = false,
                selectedBookIds = emptySet()
            )
        }
    }

    fun bulkDeleteSelected() {
        val targetIds = transient.value.selectedBookIds
        if (targetIds.isEmpty()) return
        viewModelScope.launch {
            repository.deleteBooks(targetIds)
            clearSelection()
        }
    }

    fun bulkSetReadStatus(statusCode: String) {
        val targetIds = transient.value.selectedBookIds
        if (targetIds.isEmpty()) return
        viewModelScope.launch {
            repository.updateReadStatus(targetIds, statusCode)
            clearSelection()
        }
    }

    fun bulkSetLocation(locationId: Long?) {
        val targetIds = transient.value.selectedBookIds
        if (targetIds.isEmpty()) return
        viewModelScope.launch {
            repository.updateLocationForBooks(targetIds, locationId)
            clearSelection()
        }
    }

    fun exportHtmlCatalog(uri: Uri, targetBookIds: Set<Long>? = null) {
        viewModelScope.launch {
            repository.exportHtmlCatalog(uri, targetBookIds)
        }
    }

    fun exportPdfCatalog(uri: Uri, targetBookIds: Set<Long>? = null) {
        viewModelScope.launch {
            repository.exportPdfCatalog(uri, targetBookIds)
        }
    }

    fun addMainGenre(name: String) = librarySettingsRepository.addMainGenre(name)

    fun removeMainGenre(name: String) = librarySettingsRepository.removeMainGenre(name)

    fun addSubGenre(mainGenre: String, name: String) = librarySettingsRepository.addSubGenre(mainGenre, name)

    fun removeSubGenre(mainGenre: String, name: String) = librarySettingsRepository.removeSubGenre(mainGenre, name)

    fun addBook(draft: BookDraft, onSaved: () -> Unit = {}) {
        if (draft.title.isBlank()) {
            transient.update { it.copy(errorRes = R.string.error_title_required) }
            return
        }
        viewModelScope.launch {
            val isbn = draft.isbn.normalizedIsbn()
            if (isbn.isNotBlank() && repository.hasDuplicateIsbn(isbn, ignoreBookId = draft.id.takeIf { it > 0L })) {
                transient.update { it.copy(errorRes = R.string.duplicate_isbn_warning) }
                return@launch
            }
            transient.update { it.copy(savingBookInProgress = true) }
            repository.saveBook(
                book = draft.toEntity(),
                room = draft.room,
                unit = draft.unit,
                shelf = draft.shelf,
            )
            transient.update { it.copy(errorRes = null, savingBookInProgress = false) }
            onSaved()
        }
    }

    fun deleteBook(book: BookEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteBook(book)
            onDeleted()
        }
    }

    fun moveBook(bookId: Long, room: String, unit: String, shelf: String, positionNote: String?, onMoved: () -> Unit) {
        if (room.isBlank() || unit.isBlank() || shelf.isBlank()) return
        viewModelScope.launch {
            repository.moveBook(bookId, room, unit, shelf, positionNote)
            onMoved()
        }
    }

    fun addShelf(room: String, unit: String, shelf: String, onSaved: () -> Unit) {
        if (room.isBlank() || unit.isBlank() || shelf.isBlank()) return
        viewModelScope.launch {
            repository.addShelf(room, unit, shelf)
            onSaved()
        }
    }

    fun addBorrower(name: String, phone: String, relation: String, onSaved: (Long) -> Unit) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val id = repository.addBorrower(name, phone.takeIf(String::isNotBlank), relation.takeIf(String::isNotBlank))
            onSaved(id)
        }
    }

    fun updateBookRating(bookId: Long, rating: Float) {
        viewModelScope.launch {
            repository.updateBookRating(bookId, rating)
        }
    }

    fun addQuote(bookId: Long, text: String, pageNumber: Int?, note: String?, onSaved: () -> Unit = {}) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addQuote(bookId, text, pageNumber, note)
            onSaved()
        }
    }

    fun deleteQuote(quoteId: Long) {
        viewModelScope.launch {
            repository.deleteQuote(quoteId)
        }
    }

    fun lookupIsbn(isbn: String, onResult: (BookMetadata?) -> Unit) {
        viewModelScope.launch {
            transient.update { it.copy(lookupInProgress = true, errorRes = null, failedLookupIsbn = null) }
            repository.lookupBook(isbn)
                .onSuccess { onResult(it) }
                .onFailure {
                    transient.update { state ->
                        state.copy(
                            errorRes = R.string.isbn_lookup_failed,
                            failedLookupIsbn = isbn.normalizedIsbn().takeIf(String::isNotBlank),
                        )
                    }
                    onResult(null)
                }
            transient.update { it.copy(lookupInProgress = false) }
        }
    }

    /**
     * Same lookup, but on failure it just calls [onResult] with null instead of surfacing the
     * blocking failure dialog -- used for the automatic lookup right after a scan, where a
     * miss should quietly fall through to manual entry rather than interrupt the user.
     */
    fun lookupIsbnSilently(isbn: String, onResult: (BookMetadata?) -> Unit) {
        viewModelScope.launch {
            transient.update { it.copy(lookupInProgress = true) }
            repository.lookupBook(isbn)
                .onSuccess { onResult(it) }
                .onFailure { onResult(null) }
            transient.update { it.copy(lookupInProgress = false) }
        }
    }

    fun findCoverCandidates(isbn: String, title: String, authors: List<String>, onResult: (List<String>) -> Unit) {
        viewModelScope.launch {
            onResult(repository.searchCoverCandidates(isbn, title, authors))
        }
    }

    fun searchBooksByTitleAndAuthor(title: String, authors: List<String>, onResult: (List<BookMetadata>) -> Unit) {
        viewModelScope.launch {
            transient.update { it.copy(lookupInProgress = true) }
            repository.searchBooksByTitleAndAuthor(title, authors)
                .onSuccess { onResult(it) }
                .onFailure { onResult(emptyList()) }
            transient.update { it.copy(lookupInProgress = false) }
        }
    }

    fun loanBook(draft: LoanDraft, onSaved: () -> Unit) {
        if (draft.borrowerName.isBlank()) return
        viewModelScope.launch {
            repository.loanBook(
                bookId = draft.bookId,
                borrowerId = draft.borrowerId,
                borrowerName = draft.borrowerName,
                borrowerContact = draft.borrowerContact,
                expectedReturnDateEpochMillis = draft.dueDate.toEpochMillisOrNull(),
                notes = draft.notes,
                workManager = WorkManager.getInstance(getApplication()),
                remindersEnabled = librarySettings.value.loanRemindersEnabled,
                reminderLeadDays = librarySettings.value.loanReminderLeadDays,
            )
            onSaved()
        }
    }

    fun markReturned(loanId: Long) {
        viewModelScope.launch {
            repository.markReturned(loanId)
        }
    }

    fun exportJson(uri: Uri) {
        viewModelScope.launch {
            repository.exportJson(uri)
            transient.update { it.copy(statusRes = R.string.backup_exported) }
        }
    }

    fun exportCsv(uri: Uri) {
        viewModelScope.launch {
            runCatching { repository.exportCsv(uri) }
                .onSuccess { transient.update { it.copy(statusRes = R.string.csv_exported, errorRes = null) } }
                .onFailure { transient.update { it.copy(errorRes = R.string.csv_export_failed) } }
        }
    }

    fun exportCsvTemplate(uri: Uri) {
        viewModelScope.launch {
            runCatching { repository.exportCsvTemplate(uri) }
                .onSuccess { transient.update { it.copy(statusRes = R.string.csv_template_exported, errorRes = null) } }
                .onFailure { transient.update { it.copy(errorRes = R.string.csv_export_failed) } }
        }
    }

    fun importJson(uri: Uri) {
        viewModelScope.launch {
            repository.importJson(uri)
            transient.update { it.copy(statusRes = R.string.backup_imported) }
        }
    }

    fun importCsv(uri: Uri) {
        viewModelScope.launch {
            runCatching { repository.importCsv(uri) }
                .onSuccess { transient.update { it.copy(statusRes = R.string.csv_imported, errorRes = null) } }
                .onFailure { transient.update { it.copy(errorRes = R.string.csv_import_failed) } }
        }
    }

    fun exportCompleteBackup(uri: Uri) {
        viewModelScope.launch {
            repository.exportCompleteBackup(uri)
            librarySettingsRepository.markCompleteBackupExported()
            transient.update { it.copy(statusRes = R.string.backup_exported) }
        }
    }

    fun importCompleteBackup(uri: Uri) {
        viewModelScope.launch {
            repository.importCompleteBackup(uri)
            transient.update { it.copy(statusRes = R.string.backup_imported) }
        }
    }

    fun clearError() {
        transient.update { it.copy(errorRes = null, failedLookupIsbn = null) }
    }

    fun clearStatus() {
        transient.update { it.copy(statusRes = null) }
    }

    private fun BookSortCode.comparator(): Comparator<BookListItem> =
        when (this) {
            BookSortCode.Recent -> compareByDescending { it.book.addedDateEpochMillis }
            BookSortCode.Title -> compareBy { it.book.title.lowercase() }
            BookSortCode.Author -> compareBy { it.book.authors.firstOrNull()?.lowercase().orEmpty() }
            BookSortCode.Rating -> compareByDescending { it.book.rating ?: 0f }
        }
}

@Immutable
data class HomeLibraryUiState(
    val allBooks: List<BookEntity> = emptyList(),
    val allItems: List<BookListItem> = emptyList(),
    val visibleBooks: List<BookListItem> = emptyList(),
    val locations: List<LocationEntity> = emptyList(),
    val loans: List<LoanEntity> = emptyList(),
    val activeLoans: List<LoanEntity> = emptyList(),
    val borrowers: List<BorrowerEntity> = emptyList(),
    val quotes: List<QuoteEntity> = emptyList(),
    val quotesByBookId: Map<Long, List<QuoteEntity>> = emptyMap(),
    val filters: LibraryFilters = LibraryFilters(),
    val transient: TransientState = TransientState(),
    val stats: LibraryStats = LibraryStats(),
    val itemByBookId: Map<Long, BookListItem> = emptyMap(),
    val itemsByLocationId: Map<Long, List<BookListItem>> = emptyMap(),
    val visibleItemsByLocationId: Map<Long, List<BookListItem>> = emptyMap(),
    val loansByBookId: Map<Long, List<LoanEntity>> = emptyMap(),
    val bookByIsbn: Map<String, BookEntity> = emptyMap(),
)

private data class CatalogData(
    val books: List<BookEntity>,
    val allItems: List<BookListItem>,
    val locations: List<LocationEntity>,
    val loans: List<LoanEntity>,
    val activeLoans: List<LoanEntity>,
    val itemByBookId: Map<Long, BookListItem>,
    val itemsByLocationId: Map<Long, List<BookListItem>>,
    val loansByBookId: Map<Long, List<LoanEntity>>,
    val bookByIsbn: Map<String, BookEntity>,
    val searchKeyByBookId: Map<Long, String>,
    val stats: LibraryStats,
)

@Immutable
data class BookListItem(
    val book: BookEntity,
    val location: LocationEntity?,
    val activeLoan: LoanEntity?,
) {
    val isOnLoan: Boolean = activeLoan != null
    val isOverdue: Boolean = activeLoan?.expectedReturnDateEpochMillis?.let { it < System.currentTimeMillis() } == true
}

@Immutable
data class LibraryFilters(
    val query: String = "",
    val sort: BookSortCode = BookSortCode.Recent,
    val gridMode: Boolean = true,
    val languageCode: String? = null,
    val readStatusCode: String? = null,
    val onLoanOnly: Boolean = false,
    val locationId: Long? = null,
    val tag: String? = null,
    val mainGenre: String? = null,
    val subGenre: String? = null,
) {
    val hasActiveFilters: Boolean
        get() = languageCode != null ||
            readStatusCode != null ||
            onLoanOnly ||
            locationId != null ||
            tag != null ||
            mainGenre != null ||
            subGenre != null

    fun matches(item: BookListItem, normalizedQuery: String, searchKey: String): Boolean {
        if (languageCode != null && item.book.languageCode != languageCode) return false
        if (readStatusCode != null && item.book.readStatusCode != readStatusCode) return false
        if (onLoanOnly && !item.isOnLoan) return false
        if (locationId != null && item.location?.id != locationId) return false
        if (tag != null && item.book.tags.none { it.equals(tag, ignoreCase = true) }) return false
        if (mainGenre != null && !item.book.mainGenre.equals(mainGenre, ignoreCase = true)) return false
        if (subGenre != null && item.book.subGenres.none { it.equals(subGenre, ignoreCase = true) }) return false
        return normalizedQuery.isBlank() || normalizedQuery in searchKey
    }
}

@Immutable
data class TransientState(
    val lookupInProgress: Boolean = false,
    val savingBookInProgress: Boolean = false,
    val errorRes: Int? = null,
    val statusRes: Int? = null,
    val failedLookupIsbn: String? = null,
    val selectionMode: Boolean = false,
    val selectedBookIds: Set<Long> = emptySet(),
)

data class BookDraft(
    val id: Long = 0,
    val title: String = "",
    val subtitle: String = "",
    val authors: String = "",
    val languageCode: String = LanguageCode.English.code,
    val originalScriptTitle: String = "",
    val tags: String = "",
    val isbn: String = "",
    val publisher: String = "",
    val publishedYear: String = "",
    val pageCount: String = "",
    val coverImagePath: String = "",
    val coverUrl: String = "",
    val formatCode: String = "paperback",
    val notes: String = "",
    val rating: Float = 0f,
    val readStatusCode: String = ReadStatusCode.Unread.code,
    val locationId: Long? = null,
    val room: String = "",
    val unit: String = "",
    val shelf: String = "",
    val positionNote: String = "",
    val purchaseDate: String = "",
    val cost: String = "",
    val seriesName: String = "",
    val mainGenre: String = "",
    val subGenres: List<String> = emptyList(),
    val bookType: String = "",
    val edition: String = "",
    val signedCopy: Boolean = false,
) {
    fun applyMetadata(metadata: BookMetadata): BookDraft = copy(
        title = metadata.title,
        subtitle = metadata.subtitle.orEmpty(),
        authors = metadata.authors.joinToString(", "),
        tags = metadata.tags.joinToString(", "),
        publisher = metadata.publisher.orEmpty(),
        publishedYear = metadata.publishedYear?.toString().orEmpty(),
        pageCount = metadata.pageCount?.toString().orEmpty(),
        coverImagePath = "",
        coverUrl = metadata.coverUrl.orEmpty(),
        formatCode = metadata.formatCode ?: formatCode,
        notes = metadata.notes.orEmpty(),
        languageCode = LanguageCode.fromCode(metadata.languageCode).code,
        isbn = metadata.isbn13 ?: metadata.isbn10 ?: isbn,
    )

    fun applyMissingMetadata(metadata: BookMetadata, scannedIsbn: String): BookDraft = copy(
        title = title.ifBlank { metadata.title },
        subtitle = subtitle.ifBlank { metadata.subtitle.orEmpty() },
        authors = authors.ifBlank { metadata.authors.joinToString(", ") },
        tags = tags.ifBlank { metadata.tags.joinToString(", ") },
        publisher = publisher.ifBlank { metadata.publisher.orEmpty() },
        publishedYear = publishedYear.ifBlank { metadata.publishedYear?.toString().orEmpty() },
        pageCount = pageCount.ifBlank { metadata.pageCount?.toString().orEmpty() },
        coverUrl = if (coverImagePath.isBlank() && coverUrl.isBlank()) metadata.coverUrl.orEmpty() else coverUrl,
        notes = notes.ifBlank { metadata.notes.orEmpty() },
        isbn = isbn.ifBlank { metadata.isbn13 ?: metadata.isbn10 ?: scannedIsbn },
    )

    fun toEntity(): BookEntity {
        return BookEntity(
            id = id,
            title = title.trim(),
            subtitle = subtitle.trim().takeIf(String::isNotBlank),
            authors = authors.split(",").map { it.trim() }.filter(String::isNotBlank),
            languageCode = languageCode,
            originalScriptTitle = originalScriptTitle.trim().takeIf(String::isNotBlank),
            tags = tags.split(",").map { it.trim() }.filter(String::isNotBlank),
            isbn10 = isbn.normalizedIsbn10OrNull(),
            isbn13 = isbn.normalizedIsbn13OrNull(),
            publisher = publisher.trim().takeIf(String::isNotBlank),
            publishedYear = publishedYear.toIntOrNull(),
            pageCount = pageCount.toIntOrNull(),
            coverImagePath = coverImagePath.trim().takeIf(String::isNotBlank),
            coverUrl = coverUrl.trim().takeIf(String::isNotBlank),
            formatCode = formatCode,
            notes = notes.trim().takeIf(String::isNotBlank),
            rating = rating.takeIf { it > 0f },
            readStatusCode = readStatusCode,
            locationId = locationId,
            positionNote = positionNote.trim().takeIf(String::isNotBlank),
            purchaseDateEpochMillis = purchaseDate.toEpochMillisOrNull(),
            cost = cost.toDoubleOrNull()?.takeIf { it > 0.0 },
            seriesName = seriesName.trim().takeIf(String::isNotBlank),
            mainGenre = mainGenre.trim().takeIf(String::isNotBlank),
            subGenres = subGenres,
            bookType = bookType.trim().takeIf(String::isNotBlank),
            edition = edition.trim().takeIf(String::isNotBlank),
            signedCopy = signedCopy,
        )
    }
}

data class LoanDraft(
    val bookId: Long,
    val borrowerId: Long? = null,
    val borrowerName: String = "",
    val borrowerContact: String = "",
    val dueDate: String = "",
    val notes: String = "",
)

@Immutable
data class LibraryStats(
    val totalBooks: Int = 0,
    val activeLoans: Int = 0,
    val finishedBooks: Int = 0,
    val readingBooks: Int = 0,
    val unreadBooks: Int = 0,
    val abandonedBooks: Int = 0,
    val readThisYear: Int = 0,
    val totalPages: Int = 0,
    val averageRating: Float = 0f,
    val signedCount: Int = 0,
    val languages: Map<String, Int> = emptyMap(),
    val genres: Map<String, Int> = emptyMap(),
    val topAuthors: Map<String, Int> = emptyMap(),
    val topPublishers: Map<String, Int> = emptyMap(),
    val formatsBreakdown: Map<String, Int> = emptyMap(),
    val decadesBreakdown: Map<String, Int> = emptyMap(),
    val ratingCounts: Map<Int, Int> = emptyMap(),
    val mostBorrowed: List<BookBorrowStat> = emptyList(),
    val totalLibraryValue: Double = 0.0,
) {
    companion object {
        fun from(books: List<BookEntity>, loans: List<LoanEntity>, activeLoans: List<LoanEntity>): LibraryStats {
            val currentYear = LocalDate.now().year
            val bookById = books.associateBy { it.id }

            val ratedBooks = books.filter { (it.rating ?: 0f) > 0f }
            val avgRating = if (ratedBooks.isNotEmpty()) ratedBooks.map { it.rating ?: 0f }.average().toFloat() else 0f

            val authorsMap = books.flatMap { it.authors }
                .filter { it.isNotBlank() }
                .groupingBy { it }
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .take(5)
                .associate { it.key to it.value }

            val publishersMap = books.mapNotNull { it.publisher?.trim()?.takeIf(String::isNotEmpty) }
                .groupingBy { it }
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .take(5)
                .associate { it.key to it.value }

            val formatsMap = books.groupingBy { it.formatCode.ifBlank { "paperback" } }.eachCount()

            val decadesMap = books.mapNotNull { it.publishedYear }
                .map { year ->
                    when {
                        year < 1980 -> "Pre-1980"
                        year in 1980..1989 -> "1980s"
                        year in 1990..1999 -> "1990s"
                        year in 2000..2009 -> "2000s"
                        year in 2010..2019 -> "2010s"
                        else -> "2020s"
                    }
                }
                .groupingBy { it }
                .eachCount()

            val ratingsMap = (1..5).associateWith { star ->
                books.count { (it.rating ?: 0f).toInt() == star }
            }

            return LibraryStats(
                totalBooks = books.size,
                activeLoans = activeLoans.size,
                finishedBooks = books.count { it.readStatusCode == ReadStatusCode.Finished.code },
                readingBooks = books.count { it.readStatusCode == ReadStatusCode.Reading.code },
                unreadBooks = books.count { it.readStatusCode == ReadStatusCode.Unread.code },
                abandonedBooks = books.count { it.readStatusCode == ReadStatusCode.Abandoned.code },
                readThisYear = books.count {
                    it.readStatusCode == ReadStatusCode.Finished.code &&
                        it.addedDateEpochMillis.toLocalYear() == currentYear
                },
                totalPages = books.sumOf { it.pageCount ?: 0 },
                averageRating = avgRating,
                signedCount = books.count { it.signedCopy },
                languages = books.groupingBy { it.languageCode }.eachCount(),
                genres = books.flatMap { it.subGenres }.groupingBy { it }.eachCount(),
                topAuthors = authorsMap,
                topPublishers = publishersMap,
                formatsBreakdown = formatsMap,
                decadesBreakdown = decadesMap,
                ratingCounts = ratingsMap,
                mostBorrowed = loans.groupingBy { it.bookId }
                    .eachCount()
                    .entries
                    .sortedByDescending { it.value }
                    .mapNotNull { entry ->
                        bookById[entry.key]?.let { book -> BookBorrowStat(book.title, entry.value) }
                    }
                    .take(5),
                totalLibraryValue = books.sumOf { it.cost ?: 0.0 },
            )
        }
    }
}

@Immutable
data class BookBorrowStat(
    val title: String,
    val borrowCount: Int,
)

fun BookEntity.toBookDraft(): BookDraft = BookDraft(
    id = id,
    title = title,
    subtitle = subtitle.orEmpty(),
    authors = authors.joinToString(", "),
    languageCode = languageCode,
    originalScriptTitle = originalScriptTitle.orEmpty(),
    tags = tags.joinToString(", "),
    isbn = isbn13 ?: isbn10.orEmpty(),
    publisher = publisher.orEmpty(),
    publishedYear = publishedYear?.toString().orEmpty(),
    pageCount = pageCount?.toString().orEmpty(),
    coverImagePath = coverImagePath.orEmpty(),
    coverUrl = coverUrl.orEmpty(),
    formatCode = formatCode,
    notes = notes.orEmpty(),
    rating = rating ?: 0f,
    readStatusCode = readStatusCode,
    locationId = locationId,
    positionNote = positionNote.orEmpty(),
    purchaseDate = purchaseDateEpochMillis?.toIsoDateString().orEmpty(),
    cost = cost?.let { if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString() }.orEmpty(),
    seriesName = seriesName.orEmpty(),
    mainGenre = mainGenre.orEmpty(),
    subGenres = subGenres,
    bookType = bookType.orEmpty(),
    edition = edition.orEmpty(),
    signedCopy = signedCopy,
)

private fun Long.toIsoDateString(): String =
    java.time.Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate().toString()

private fun Long.toLocalYear(): Int =
    java.time.Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).year

private val CombiningMarksRegex = Regex("\\p{InCombiningDiacriticalMarks}+")

private fun BookEntity.toSearchKey(): String = buildString {
    append(title)
    append(' ')
    append(subtitle.orEmpty())
    append(' ')
    append(originalScriptTitle.orEmpty())
    append(' ')
    append(authors.joinToString(" "))
    append(' ')
    append(tags.joinToString(" "))
    append(' ')
    append(notes.orEmpty())
    append(' ')
    append(isbn10.orEmpty())
    append(' ')
    append(isbn13.orEmpty())
    append(' ')
    append(seriesName.orEmpty())
    append(' ')
    append(mainGenre.orEmpty())
    append(' ')
    append(subGenres.joinToString(" "))
    append(' ')
    append(edition.orEmpty())
}.searchKey()

private fun String.searchKey(): String =
    Normalizer.normalize(lowercase(), Normalizer.Form.NFD)
        .replace(CombiningMarksRegex, "")

private fun String.toEpochMillisOrNull(): Long? =
    runCatching {
        LocalDate.parse(this).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }.getOrNull()
