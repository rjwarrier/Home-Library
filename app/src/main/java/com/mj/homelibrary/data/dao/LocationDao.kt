package com.mj.homelibrary.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mj.homelibrary.data.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations ORDER BY room COLLATE NOCASE, unit COLLATE NOCASE, sortOrder, shelf COLLATE NOCASE")
    fun observeLocations(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations")
    suspend fun getAll(): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE room = :room AND unit = :unit AND shelf = :shelf LIMIT 1")
    suspend fun find(room: String, unit: String, shelf: String): LocationEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(location: LocationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locations: List<LocationEntity>)

    @Query("DELETE FROM locations")
    suspend fun clear()

    suspend fun getOrCreate(room: String, unit: String, shelf: String): Long {
        val existing = find(room.trim(), unit.trim(), shelf.trim())
        if (existing != null) return existing.id
        val inserted = insert(LocationEntity(room = room.trim(), unit = unit.trim(), shelf = shelf.trim()))
        return if (inserted > 0) inserted else find(room.trim(), unit.trim(), shelf.trim())?.id ?: inserted
    }
}
