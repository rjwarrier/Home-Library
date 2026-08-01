package com.mj.homelibrary.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.mj.homelibrary.HomeLibraryApplication
import com.mj.homelibrary.R
import com.mj.homelibrary.data.BookSortCode
import com.mj.homelibrary.data.LanguageCode
import com.mj.homelibrary.data.ReadStatusCode
import com.mj.homelibrary.data.entity.BookEntity
import com.mj.homelibrary.data.entity.LoanEntity
import com.mj.homelibrary.data.entity.LocationEntity
import com.mj.homelibrary.data.remote.BookMetadata
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
    private val repository = (application as HomeLibraryApplication).repository
    private val filters = MutableStateFlow(LibraryFilters())
    private val transient = MutableStateFlow(TransientState())

    val state: StateFlow<HomeLibraryUiState> = combine(
        repository.books,
        repository.locations,
        repository.loans,
        filters,
        transient,
    ) { books, locations, loans, filters, transient ->
        val activeLoans = loans.filter { it.actualReturnDateEpochMillis == null }
        val items = books.map { book ->
            BookListItem(
                book = book,
                location = locations.firstOrNull { it.id == book.locationId },
                activeLoan = activeLoans.firstOrNull { it.bookId == book.id },
            )
        }.filter(filters::matches)
            .sortedWith(filters.sort.comparator())

        HomeLibraryUiState(
            allBooks = books,
            visibleBooks = items,
            locations = locations,
            loans = loans,
            activeLoans = activeLoans,
            filters = filters,
            transient = transient,
            stats = LibraryStats.from(books, activeLoans),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeLibraryUiState(),
    )

    fun setQuery(query: String) {
        filters.update { it.copy(query = query) }
    }

    fun setSort(sort: BookSortCode) {
        filters.update { it.copy(sort = sort) }
    }

    fun setGridMode(enabled: Boolean) {
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

    fun addBook(draft: BookDraft, onSaved: () -> Unit) {
        if (draft.title.isBlank()) {
            transient.update { it.copy(errorRes = R.string.error_title_required) }
            return
        }
        viewModelScope.launch {
            val isbn = draft.isbn.filter(Char::isDigit)
            if (isbn.isNotBlank() && repository.hasDuplicateIsbn(isbn)) {
                transient.update { it.copy(errorRes = R.string.duplicate_isbn_warning) }
                return@launch
            }
            repository.saveBook(
                book = draft.toEntity(),
                room = draft.room,
                unit = draft.unit,
                shelf = draft.shelf,
            )
            transient.update { it.copy(errorRes = null) }
            onSaved()
        }
    }

    fun lookupIsbn(isbn: String, onResult: (BookMetadata?) -> Unit) {
        viewModelScope.launch {
            transient.update { it.copy(lookupInProgress = true, errorRes = null) }
            repository.lookupBook(isbn)
                .onSuccess { onResult(it) }
                .onFailure {
                    transient.update { state -> state.copy(errorRes = R.string.isbn_lookup_failed) }
                    onResult(null)
                }
            transient.update { it.copy(lookupInProgress = false) }
        }
    }

    fun loanBook(draft: LoanDraft, onSaved: () -> Unit) {
        if (draft.borrowerName.isBlank()) return
        viewModelScope.launch {
            repository.loanBook(
                bookId = draft.bookId,
                borrowerName = draft.borrowerName,
                borrowerContact = draft.borrowerContact,
                expectedReturnDateEpochMillis = draft.dueDate.toEpochMillisOrNull(),
                notes = draft.notes,
                workManager = WorkManager.getInstance(getApplication()),
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
            repository.exportCsv(uri)
            transient.update { it.copy(statusRes = R.string.backup_exported) }
        }
    }

    fun importJson(uri: Uri) {
        viewModelScope.launch {
            repository.importJson(uri)
            transient.update { it.copy(statusRes = R.string.backup_imported) }
        }
    }

    fun clearError() {
        transient.update { it.copy(errorRes = null) }
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

data class HomeLibraryUiState(
    val allBooks: List<BookEntity> = emptyList(),
    val visibleBooks: List<BookListItem> = emptyList(),
    val locations: List<LocationEntity> = emptyList(),
    val loans: List<LoanEntity> = emptyList(),
    val activeLoans: List<LoanEntity> = emptyList(),
    val filters: LibraryFilters = LibraryFilters(),
    val transient: TransientState = TransientState(),
    val stats: LibraryStats = LibraryStats(),
)

data class BookListItem(
    val book: BookEntity,
    val location: LocationEntity?,
    val activeLoan: LoanEntity?,
) {
    val isOnLoan: Boolean = activeLoan != null
    val isOverdue: Boolean = activeLoan?.expectedReturnDateEpochMillis?.let { it < System.currentTimeMillis() } == true
}

data class LibraryFilters(
    val query: String = "",
    val sort: BookSortCode = BookSortCode.Recent,
    val gridMode: Boolean = true,
    val languageCode: String? = null,
    val readStatusCode: String? = null,
    val onLoanOnly: Boolean = false,
    val locationId: Long? = null,
) {
    fun matches(item: BookListItem): Boolean {
        if (languageCode != null && item.book.languageCode != languageCode) return false
        if (readStatusCode != null && item.book.readStatusCode != readStatusCode) return false
        if (onLoanOnly && !item.isOnLoan) return false
        if (locationId != null && item.location?.id != locationId) return false
        if (query.isBlank()) return true
        val normalizedQuery = query.searchKey()
        val haystack = buildString {
            append(item.book.title)
            append(' ')
            append(item.book.subtitle.orEmpty())
            append(' ')
            append(item.book.originalScriptTitle.orEmpty())
            append(' ')
            append(item.book.authors.joinToString(" "))
            append(' ')
            append(item.book.tags.joinToString(" "))
            append(' ')
            append(item.book.notes.orEmpty())
            append(' ')
            append(item.book.isbn10.orEmpty())
            append(' ')
            append(item.book.isbn13.orEmpty())
        }.searchKey()
        return normalizedQuery in haystack
    }
}

data class TransientState(
    val lookupInProgress: Boolean = false,
    val errorRes: Int? = null,
    val statusRes: Int? = null,
)

data class BookDraft(
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
    val coverUrl: String = "",
    val formatCode: String = "paperback",
    val notes: String = "",
    val rating: Float = 0f,
    val readStatusCode: String = ReadStatusCode.Unread.code,
    val room: String = "",
    val unit: String = "",
    val shelf: String = "",
    val positionNote: String = "",
) {
    fun applyMetadata(metadata: BookMetadata): BookDraft = copy(
        title = metadata.title,
        subtitle = metadata.subtitle.orEmpty(),
        authors = metadata.authors.joinToString(", "),
        publisher = metadata.publisher.orEmpty(),
        publishedYear = metadata.publishedYear?.toString().orEmpty(),
        pageCount = metadata.pageCount?.toString().orEmpty(),
        coverUrl = metadata.coverUrl.orEmpty(),
        languageCode = LanguageCode.fromCode(metadata.languageCode).code,
        isbn = metadata.isbn13 ?: metadata.isbn10 ?: isbn,
    )

    fun toEntity(): BookEntity {
        val normalizedIsbn = isbn.filter(Char::isDigit)
        return BookEntity(
            title = title.trim(),
            subtitle = subtitle.trim().takeIf(String::isNotBlank),
            authors = authors.split(",").map { it.trim() }.filter(String::isNotBlank),
            languageCode = languageCode,
            originalScriptTitle = originalScriptTitle.trim().takeIf(String::isNotBlank),
            tags = tags.split(",").map { it.trim() }.filter(String::isNotBlank),
            isbn10 = normalizedIsbn.takeIf { it.length == 10 },
            isbn13 = normalizedIsbn.takeIf { it.length == 13 },
            publisher = publisher.trim().takeIf(String::isNotBlank),
            publishedYear = publishedYear.toIntOrNull(),
            pageCount = pageCount.toIntOrNull(),
            coverUrl = coverUrl.trim().takeIf(String::isNotBlank),
            formatCode = formatCode,
            notes = notes.trim().takeIf(String::isNotBlank),
            rating = rating.takeIf { it > 0f },
            readStatusCode = readStatusCode,
            positionNote = positionNote.trim().takeIf(String::isNotBlank),
        )
    }
}

data class LoanDraft(
    val bookId: Long,
    val borrowerName: String = "",
    val borrowerContact: String = "",
    val dueDate: String = "",
    val notes: String = "",
)

data class LibraryStats(
    val totalBooks: Int = 0,
    val activeLoans: Int = 0,
    val finishedBooks: Int = 0,
    val languages: Map<String, Int> = emptyMap(),
    val genres: Map<String, Int> = emptyMap(),
) {
    companion object {
        fun from(books: List<BookEntity>, activeLoans: List<LoanEntity>) = LibraryStats(
            totalBooks = books.size,
            activeLoans = activeLoans.size,
            finishedBooks = books.count { it.readStatusCode == ReadStatusCode.Finished.code },
            languages = books.groupingBy { it.languageCode }.eachCount(),
            genres = books.flatMap { it.tags }.groupingBy { it }.eachCount(),
        )
    }
}

private fun String.searchKey(): String =
    Normalizer.normalize(lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")

private fun String.toEpochMillisOrNull(): Long? =
    runCatching {
        LocalDate.parse(this).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }.getOrNull()
