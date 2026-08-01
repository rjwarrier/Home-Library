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
    fun observeBook(id: Long): Flow<BookEntity?>

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

    @Delete
    suspend fun delete(book: BookEntity)

    @Query("DELETE FROM books")
    suspend fun clear()
}
