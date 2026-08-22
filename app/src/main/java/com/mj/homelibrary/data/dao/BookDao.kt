package com.mj.homelibrary.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mj.homelibrary.data.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY addedDateEpochMillis DESC")
    fun observeBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun get(id: Long): BookEntity?

    @Query("SELECT * FROM books WHERE isbn10 = :isbn OR isbn13 = :isbn LIMIT 1")
    suspend fun findByIsbn(isbn: String): BookEntity?

    @Query("SELECT * FROM books")
    suspend fun getAll(): List<BookEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<BookEntity>)

    @Update
    suspend fun update(book: BookEntity)

    @Query("UPDATE books SET locationId = :locationId WHERE id IN (:bookIds)")
    suspend fun updateLocations(bookIds: Set<Long>, locationId: Long)

    @Query("UPDATE books SET locationId = :locationId, positionNote = :positionNote WHERE id = :bookId")
    suspend fun updateLocation(bookId: Long, locationId: Long, positionNote: String?)

    @Query("UPDATE books SET rating = :rating WHERE id = :bookId")
    suspend fun updateRating(bookId: Long, rating: Float?)

    @Delete
    suspend fun delete(book: BookEntity)

    @Query("DELETE FROM books WHERE id IN (:bookIds)")
    suspend fun deleteBooks(bookIds: Set<Long>)

    @Query("UPDATE books SET readStatusCode = :statusCode WHERE id IN (:bookIds)")
    suspend fun updateReadStatus(bookIds: Set<Long>, statusCode: String)

    @Query("UPDATE books SET locationId = :locationId WHERE id IN (:bookIds)")
    suspend fun updateLocationForBooks(bookIds: Set<Long>, locationId: Long?)

    @Query("DELETE FROM books")
    suspend fun clear()
}
