package com.mj.homelibrary.data.remote

data class BookMetadata(
    val title: String,
    val subtitle: String? = null,
    val authors: List<String> = emptyList(),
    val publisher: String? = null,
    val publishedYear: Int? = null,
    val pageCount: Int? = null,
    val coverUrl: String? = null,
    val languageCode: String = "en",
    val isbn10: String? = null,
    val isbn13: String? = null,
)
