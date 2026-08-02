package com.mj.homelibrary.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mj.homelibrary.data.entity.BorrowerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BorrowerDao {
    @Query("SELECT * FROM borrowers ORDER BY name")
    fun observeBorrowers(): Flow<List<BorrowerEntity>>

    @Query("SELECT * FROM borrowers WHERE id = :id")
    suspend fun get(id: Long): BorrowerEntity?

    @Query("SELECT * FROM borrowers")
    suspend fun getAll(): List<BorrowerEntity>

    @Insert
    suspend fun insert(borrower: BorrowerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(borrowers: List<BorrowerEntity>)

    @Query("DELETE FROM borrowers")
    suspend fun clear()
}
