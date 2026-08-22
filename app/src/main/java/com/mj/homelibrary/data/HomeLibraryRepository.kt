package com.mj.homelibrary.data

import android.net.Uri
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mj.homelibrary.data.dao.BookDao
import com.mj.homelibrary.data.dao.BorrowerDao
import com.mj.homelibrary.data.dao.LoanDao
import com.mj.homelibrary.data.dao.LocationDao
import com.mj.homelibrary.data.entity.BookEntity
import com.mj.homelibrary.data.entity.BorrowerEntity
import com.mj.homelibrary.data.entity.LoanEntity
import com.mj.homelibrary.data.entity.LocationEntity
import com.mj.homelibrary.data.remote.BookLookupService
import com.mj.homelibrary.data.remote.BookMetadata
import com.mj.homelibrary.worker.LoanReminderWorker
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class HomeLibraryRepository(
    private val bookDao: BookDao,
    private val locationDao: LocationDao,
    private val loanDao: LoanDao,
    private val borrowerDao: BorrowerDao,
    private val lookupService: BookLookupService,
    private val backupRepository: BackupRepository,
    private val coverCache: CoverCache,
) {
    val books: Flow<List<BookEntity>> = bookDao.observeBooks()
    val locations: Flow<List<LocationEntity>> = locationDao.observeLocations()
    val loans: Flow<List<LoanEntity>> = loanDao.observeLoans()
    val borrowers: Flow<List<BorrowerEntity>> = borrowerDao.observeBorrowers()

    suspend fun addBorrower(name: String, phone: String?, relation: String?): Long =
        borrowerDao.insert(
            BorrowerEntity(
                name = name.trim(),
                phone = phone?.trim()?.takeIf(String::isNotBlank),
                relation = relation?.trim()?.takeIf(String::isNotBlank),
            ),
        )

    suspend fun lookupBook(isbn: String): Result<BookMetadata> = lookupService.lookup(isbn)

    suspend fun searchCoverCandidates(isbn: String, title: String, authors: List<String>): List<String> =
        lookupService.searchCoverCandidates(isbn, title, authors)

    suspend fun searchBooksByTitleAndAuthor(title: String, authors: List<String>): Result<List<BookMetadata>> =
        lookupService.searchByTitleAndAuthor(title, authors)

    suspend fun hasDuplicateIsbn(isbn: String, ignoreBookId: Long? = null): Boolean {
        val normalized = isbn.normalizedIsbn()
        val existing = if (normalized.isNotBlank()) bookDao.findByIsbn(normalized) else null
        return existing != null && existing.id != ignoreBookId
    }

    suspend fun saveBook(
        book: BookEntity,
        room: String?,
        unit: String?,
        shelf: String?,
    ): Long {
        val locationId = if (!room.isNullOrBlank() && !unit.isNullOrBlank() && !shelf.isNullOrBlank()) {
            locationDao.getOrCreate(room, unit, shelf)
        } else {
            book.locationId
        }
        val cachedCoverPath = book.coverImagePath ?: coverCache.cache(book.coverUrl)
        val bookToSave = book.copy(locationId = locationId, coverImagePath = cachedCoverPath)
        return if (book.id == 0L) {
            bookDao.insert(bookToSave)
        } else {
            bookDao.update(bookToSave)
            book.id
        }
    }

    suspend fun moveBook(bookId: Long, room: String, unit: String, shelf: String, positionNote: String?) {
        val locationId = locationDao.getOrCreate(room, unit, shelf)
        bookDao.updateLocation(
            bookId = bookId,
            locationId = locationId,
            positionNote = positionNote?.trim()?.takeIf(String::isNotBlank),
        )
    }

    suspend fun addShelf(room: String, unit: String, shelf: String) {
        if (room.isBlank() || unit.isBlank() || shelf.isBlank()) return
        locationDao.getOrCreate(room, unit, shelf)
    }

    suspend fun updateBookRating(bookId: Long, rating: Float) {
        bookDao.updateRating(bookId, rating.takeIf { it > 0f })
    }

    suspend fun deleteBook(book: BookEntity) {
        bookDao.delete(book)
    }

    suspend fun deleteBooks(bookIds: Set<Long>) {
        forEachIdChunk(bookIds) { bookDao.deleteBooks(it) }
    }

    suspend fun updateReadStatus(bookIds: Set<Long>, statusCode: String) {
        forEachIdChunk(bookIds) { bookDao.updateReadStatus(it, statusCode) }
    }

    suspend fun updateLocationForBooks(bookIds: Set<Long>, locationId: Long?) {
        forEachIdChunk(bookIds) { bookDao.updateLocationForBooks(it, locationId) }
    }

    /**
     * Splits a bulk `WHERE id IN (:ids)` operation into chunks so it stays under SQLite's
     * per-statement bound-parameter limit (SQLITE_MAX_VARIABLE_NUMBER, as low as 999 on
     * some Android builds) when the user selects a large number of books at once.
     */
    private suspend fun forEachIdChunk(ids: Set<Long>, action: suspend (Set<Long>) -> Unit) {
        if (ids.isEmpty()) return
        ids.chunked(SQL_IN_CLAUSE_CHUNK_SIZE).forEach { action(it.toSet()) }
    }

    suspend fun exportHtmlCatalog(uri: Uri, targetBookIds: Set<Long>? = null) = backupRepository.exportHtmlCatalog(uri, targetBookIds)

    suspend fun exportPdfCatalog(uri: Uri, targetBookIds: Set<Long>? = null) = backupRepository.exportPdfCatalog(uri, targetBookIds)

    suspend fun loanBook(
        bookId: Long,
        borrowerId: Long?,
        borrowerName: String,
        borrowerContact: String?,
        expectedReturnDateEpochMillis: Long?,
        notes: String?,
        workManager: WorkManager,
        remindersEnabled: Boolean,
        reminderLeadDays: Int,
    ) {
        val existing = loanDao.activeLoanForBook(bookId)
        if (existing != null) return
        val loanId = loanDao.insert(
            LoanEntity(
                bookId = bookId,
                borrowerId = borrowerId,
                borrowerName = borrowerName.trim(),
                borrowerContact = borrowerContact?.trim()?.takeIf(String::isNotBlank),
                expectedReturnDateEpochMillis = expectedReturnDateEpochMillis,
                notes = notes?.trim()?.takeIf(String::isNotBlank),
            ),
        )
        if (expectedReturnDateEpochMillis != null && remindersEnabled) {
            enqueueLoanReminder(workManager, loanId, bookId, expectedReturnDateEpochMillis, reminderLeadDays)
        }
    }

    suspend fun markReturned(loanId: Long) {
        loanDao.markReturned(loanId, System.currentTimeMillis())
    }

    suspend fun exportJson(uri: Uri) = backupRepository.exportJson(uri)

    suspend fun exportCsv(uri: Uri) = backupRepository.exportCsv(uri)

    suspend fun exportCsvTemplate(uri: Uri) = backupRepository.exportCsvTemplate(uri)

    suspend fun importJson(uri: Uri) = backupRepository.importJson(uri)

    suspend fun importCsv(uri: Uri) = backupRepository.importCsv(uri)

    suspend fun exportCompleteBackup(uri: Uri) = backupRepository.exportCompleteBackup(uri)

    suspend fun importCompleteBackup(uri: Uri) = backupRepository.importCompleteBackup(uri)

    private fun enqueueLoanReminder(
        workManager: WorkManager,
        loanId: Long,
        bookId: Long,
        dueEpochMillis: Long,
        leadDays: Int,
    ) {
        val leadMillis = TimeUnit.DAYS.toMillis(leadDays.coerceAtLeast(0).toLong())
        val delay = (dueEpochMillis - leadMillis - System.currentTimeMillis()).coerceAtLeast(0L)
        val input = Data.Builder()
            .putLong(LoanReminderWorker.KEY_LOAN_ID, loanId)
            .putLong(LoanReminderWorker.KEY_BOOK_ID, bookId)
            .build()
        val request = OneTimeWorkRequestBuilder<LoanReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(input)
            .build()
        workManager.enqueue(request)
    }

    private companion object {
        const val SQL_IN_CLAUSE_CHUNK_SIZE = 900
    }
}
