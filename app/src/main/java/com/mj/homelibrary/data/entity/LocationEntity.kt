package com.mj.homelibrary.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "locations",
    indices = [Index(value = ["room", "unit", "shelf"], unique = true)],
)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val room: String,
    val unit: String,
    val shelf: String,
    val sortOrder: Int = 0,
)
