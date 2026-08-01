package com.mj.homelibrary.data

import android.net.Uri
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mj.homelibrary.data.dao.BookDao
import com.mj.homelibrary.data.dao.LoanDao
import com.mj.homelibrary.data.dao.LocationDao
import com.mj.homelibrary.data.entity.BookEntity
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
    private val lookupService: BookLookupService,
    private val backupRepository: BackupRepository,
    private val coverCache: CoverCache,
) {
    val books: Flow<List<BookEntity>> = bookDao.observeBooks()
    val locations: Flow<List<LocationEntity>> = locationDao.observeLocations()
    val loans: Flow<List<LoanEntity>> = loanDao.observeLoans()

    suspend fun lookupBook(isbn: String): Result<BookMetadata> = lookupService.lookup(isbn)

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

    suspend fun moveBooks(bookIds: Set<Long>, locationId: Long) {
        bookDao.getAll()
            .filter { it.id in bookIds }
            .forEach { bookDao.update(it.copy(locationId = locationId)) }
    }

    suspend fun moveBook(bookId: Long, room: String, unit: String, shelf: String, positionNote: String?) {
        val book = bookDao.getAll().firstOrNull { it.id == bookId } ?: return
        val locationId = locationDao.getOrCreate(room, unit, shelf)
        bookDao.update(book.copy(locationId = locationId, positionNote = positionNote?.trim()?.takeIf(String::isNotBlank)))
    }

    suspend fun addShelf(room: String, unit: String, shelf: String) {
        if (room.isBlank() || unit.isBlank() || shelf.isBlank()) return
        locationDao.getOrCreate(room, unit, shelf)
    }

    suspend fun updateBookRating(bookId: Long, rating: Float) {
        val book = bookDao.getAll().firstOrNull { it.id == bookId } ?: return
        bookDao.update(book.copy(rating = rating.takeIf { it > 0f }))
    }

    suspend fun deleteBook(book: BookEntity) {
        bookDao.delete(book)
    }

    suspend fun loanBook(
        bookId: Long,
        borrowerName: String,
        borrowerContact: String?,
        expectedReturnDateEpochMillis: Long?,
        notes: String?,
        workManager: WorkManager,
    ) {
        val existing = loanDao.activeLoanForBook(bookId)
        if (existing != null) return
        val loanId = loanDao.insert(
            LoanEntity(
                bookId = bookId,
                borrowerName = borrowerName.trim(),
                borrowerContact = borrowerContact?.trim()?.takeIf(String::isNotBlank),
                expectedReturnDateEpochMillis = expectedReturnDateEpochMillis,
                notes = notes?.trim()?.takeIf(String::isNotBlank),
            ),
        )
        if (expectedReturnDateEpochMillis != null) {
            enqueueLoanReminder(workManager, loanId, bookId, expectedReturnDateEpochMillis)
        }
    }

    suspend fun markReturned(loanId: Long) {
        val loan = loanDao.get(loanId) ?: return
        loanDao.update(loan.copy(actualReturnDateEpochMillis = System.currentTimeMillis()))
    }

    suspend fun exportJson(uri: Uri) = backupRepository.exportJson(uri)

    suspend fun exportCsv(uri: Uri) = backupRepository.exportCsv(uri)

    suspend fun importJson(uri: Uri) = backupRepository.importJson(uri)

    suspend fun importCsv(uri: Uri) = backupRepository.importCsv(uri)

    suspend fun exportCompleteBackup(uri: Uri) = backupRepository.exportCompleteBackup(uri)

    suspend fun importCompleteBackup(uri: Uri) = backupRepository.importCompleteBackup(uri)

    private fun enqueueLoanReminder(
        workManager: WorkManager,
        loanId: Long,
        bookId: Long,
        dueEpochMillis: Long,
    ) {
        val delay = (dueEpochMillis - System.currentTimeMillis()).coerceAtLeast(0L)
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
}
