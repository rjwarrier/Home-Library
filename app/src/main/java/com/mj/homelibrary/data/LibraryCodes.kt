package com.mj.homelibrary.data

import androidx.annotation.StringRes
import com.mj.homelibrary.R

enum class BookFormatCode(val code: String, @StringRes val labelRes: Int) {
    Hardcover("hardcover", R.string.format_hardcover),
    Paperback("paperback", R.string.format_paperback),
    Ebook("ebook", R.string.format_ebook),
    Other("other", R.string.format_other);

    companion object {
        fun fromCode(code: String): BookFormatCode = entries.firstOrNull { it.code == code } ?: Other
    }
}

enum class ReadStatusCode(val code: String, @StringRes val labelRes: Int) {
    Unread("unread", R.string.status_unread),
    Reading("reading", R.string.status_reading),
    Finished("finished", R.string.status_finished);

    companion object {
        fun fromCode(code: String): ReadStatusCode = entries.firstOrNull { it.code == code } ?: Unread
    }
}

enum class LanguageCode(val code: String, @StringRes val labelRes: Int) {
    English("en", R.string.language_en),
    Malayalam("ml", R.string.language_ml),
    Hindi("hi", R.string.language_hi),
    Other("other", R.string.language_other);

    companion object {
        fun fromCode(code: String): LanguageCode = entries.firstOrNull { it.code == code } ?: Other
    }
}

enum class BookSortCode(@StringRes val labelRes: Int) {
    Recent(R.string.sort_recent),
    Title(R.string.sort_title),
    Author(R.string.sort_author),
    Rating(R.string.sort_rating),
}
