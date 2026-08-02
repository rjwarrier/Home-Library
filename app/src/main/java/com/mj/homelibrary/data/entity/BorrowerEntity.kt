package com.mj.homelibrary.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "borrowers")
data class BorrowerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String? = null,
    val relation: String? = null,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
)
