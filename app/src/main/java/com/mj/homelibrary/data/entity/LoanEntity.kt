package com.mj.homelibrary.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "loans",
    indices = [
        Index(value = ["bookId"]),
        Index(value = ["borrowerName"]),
        Index(value = ["actualReturnDateEpochMillis"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val borrowerName: String,
    val borrowerContact: String? = null,
    val loanDateEpochMillis: Long = System.currentTimeMillis(),
    val expectedReturnDateEpochMillis: Long? = null,
    val actualReturnDateEpochMillis: Long? = null,
    val notes: String? = null,
    val borrowerId: Long? = null,
)
