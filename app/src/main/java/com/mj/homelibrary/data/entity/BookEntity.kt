package com.mj.homelibrary.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    indices = [
        Index(value = ["isbn10"]),
        Index(value = ["isbn13"], unique = true),
        Index(value = ["locationId"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
)
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String? = null,
    val authors: List<String> = emptyList(),
    val languageCode: String = "en",
    val originalScriptTitle: String? = null,
    val tags: List<String> = emptyList(),
    val isbn10: String? = null,
    val isbn13: String? = null,
    val publisher: String? = null,
    val publishedYear: Int? = null,
    val pageCount: Int? = null,
    val coverImagePath: String? = null,
    val coverUrl: String? = null,
    val formatCode: String = "paperback",
    val acquisitionDateEpochMillis: Long? = null,
    val notes: String? = null,
    val rating: Float? = null,
    val readStatusCode: String = "unread",
    val locationId: Long? = null,
    val positionNote: String? = null,
    val addedDateEpochMillis: Long = System.currentTimeMillis(),
    val purchaseDateEpochMillis: Long? = null,
    val cost: Double? = null,
    val seriesName: String? = null,
)
