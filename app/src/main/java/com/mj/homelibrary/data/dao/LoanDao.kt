package com.mj.homelibrary.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mj.homelibrary.data.entity.LoanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans ORDER BY loanDateEpochMillis DESC")
    fun observeLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE actualReturnDateEpochMillis IS NULL ORDER BY expectedReturnDateEpochMillis")
    fun observeActiveLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE bookId = :bookId ORDER BY loanDateEpochMillis DESC")
    fun observeLoansForBook(bookId: Long): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans")
    suspend fun getAll(): List<LoanEntity>

    @Query("SELECT * FROM loans WHERE id = :id")
    suspend fun get(id: Long): LoanEntity?

    @Query("SELECT * FROM loans WHERE bookId = :bookId AND actualReturnDateEpochMillis IS NULL LIMIT 1")
    suspend fun activeLoanForBook(bookId: Long): LoanEntity?

    @Insert
    suspend fun insert(loan: LoanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(loans: List<LoanEntity>)

    @Update
    suspend fun update(loan: LoanEntity)

    @Query("UPDATE loans SET actualReturnDateEpochMillis = :returnedAtEpochMillis WHERE id = :loanId")
    suspend fun markReturned(loanId: Long, returnedAtEpochMillis: Long)

    @Query("DELETE FROM loans")
    suspend fun clear()
}
