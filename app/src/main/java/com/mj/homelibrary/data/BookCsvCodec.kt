package com.mj.homelibrary.data

import com.mj.homelibrary.data.entity.BookEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

internal data class BookCsvRow(
    val book: BookEntity,
    val room: String? = null,
    val bookcase: String? = null,
    val shelf: String? = null,
)

internal object BookCsvCodec {
    private val columns = listOf(
        "title",
        "authors",
        "language",
        "isbn13",
        "isbn10",
        "publisher",
        "published_year",
        "tags",
        "read_status",
        "rating",
        "subtitle",
        "original_script_title",
        "page_count",
        "format_code",
        "acquisition_date",
        "notes",
        "room",
        "bookcase",
        "shelf",
        "position_note",
        "added_date",
        "purchase_date",
        "cost",
        "series_name",
        "main_genre",
        "sub_genres",
        "book_type",
        "edition",
        "signed_copy",
    )

    private val aliases = mapOf(
        "author" to "authors",
        "language_code" to "language",
        "publication_year" to "published_year",
        "format" to "format_code",
        "read_status_code" to "read_status",
        "unit" to "bookcase",
        "shelf_row" to "shelf",
        "series" to "series_name",
        "subgenres" to "sub_genres",
        "signed" to "signed_copy",
    )

    fun encode(rows: List<BookCsvRow>): String =
        encodeRecords(listOf(columns) + rows.map { it.toRecord() })

    fun template(): String = encodeRecords(listOf(columns))

    fun decode(text: String): List<BookCsvRow> {
        val records = parseRecords(text).filterNot { record -> record.all(String::isBlank) }
        require(records.isNotEmpty()) { "CSV file is empty" }
        val header = records.first().mapIndexed { index, value ->
            (aliases[value.headerKey()] ?: value.headerKey()) to index
        }.toMap()
        require(header.containsKey("title")) { "CSV file must contain a title column" }

        return records.drop(1).mapNotNull { record -> record.toBookCsvRow(header) }
    }

    private fun BookCsvRow.toRecord(): List<String> = listOf(
        book.title,
        book.authors.joinToString("; "),
        book.languageCode,
        book.isbn13.orEmpty(),
        book.isbn10.orEmpty(),
        book.publisher.orEmpty(),
        book.publishedYear?.toString().orEmpty(),
        book.tags.joinToString("; "),
        book.readStatusCode,
        book.rating?.toString().orEmpty(),
        book.subtitle.orEmpty(),
        book.originalScriptTitle.orEmpty(),
        book.pageCount?.toString().orEmpty(),
        book.formatCode,
        book.acquisitionDateEpochMillis.toCsvDate(),
        book.notes.orEmpty(),
        room.orEmpty(),
        bookcase.orEmpty(),
        shelf.orEmpty(),
        book.positionNote.orEmpty(),
        book.addedDateEpochMillis.toCsvDate(),
        book.purchaseDateEpochMillis.toCsvDate(),
        book.cost?.toString().orEmpty(),
        book.seriesName.orEmpty(),
        book.mainGenre.orEmpty(),
        book.subGenres.joinToString("; "),
        book.bookType.orEmpty(),
        book.edition.orEmpty(),
        book.signedCopy.toString(),
    )

    private fun List<String>.toBookCsvRow(header: Map<String, Int>): BookCsvRow? {
        fun value(column: String): String? =
            header[column]?.let(::getOrNull)?.trim()?.takeIf(String::isNotBlank)

        val title = value("title") ?: return null
        return BookCsvRow(
            book = BookEntity(
                title = title,
                subtitle = value("subtitle"),
                authors = value("authors").semicolonList(),
                languageCode = value("language") ?: "en",
                originalScriptTitle = value("original_script_title"),
                tags = value("tags").semicolonList(),
                isbn10 = value("isbn10")?.normalizedIsbn10OrNull(),
                isbn13 = value("isbn13")?.normalizedIsbn13OrNull(),
                publisher = value("publisher"),
                publishedYear = value("published_year")?.toIntOrNull(),
                pageCount = value("page_count")?.toIntOrNull()?.takeIf { it > 0 },
                formatCode = value("format_code") ?: "paperback",
                acquisitionDateEpochMillis = value("acquisition_date").toEpochMillisOrNull(),
                notes = value("notes"),
                rating = value("rating")?.toFloatOrNull()?.takeIf { it > 0f },
                readStatusCode = value("read_status") ?: "unread",
                positionNote = value("position_note"),
                addedDateEpochMillis = value("added_date").toEpochMillisOrNull() ?: System.currentTimeMillis(),
                purchaseDateEpochMillis = value("purchase_date").toEpochMillisOrNull(),
                cost = value("cost")?.toDoubleOrNull(),
                seriesName = value("series_name"),
                mainGenre = value("main_genre"),
                subGenres = value("sub_genres").semicolonList(),
                bookType = value("book_type"),
                edition = value("edition"),
                signedCopy = value("signed_copy").toCsvBoolean(),
            ),
            room = value("room"),
            bookcase = value("bookcase"),
            shelf = value("shelf"),
        )
    }

    private fun encodeRecords(records: List<List<String>>): String =
        records.joinToString(separator = "\r\n", postfix = "\r\n") { record ->
            record.joinToString(",") { it.csvEscape() }
        }

    private fun parseRecords(text: String): List<List<String>> {
        val records = mutableListOf<List<String>>()
        val record = mutableListOf<String>()
        val value = StringBuilder()
        var quoted = false
        var index = 0

        fun finishValue() {
            record += value.toString()
            value.clear()
        }

        fun finishRecord() {
            finishValue()
            records += record.toList()
            record.clear()
        }

        while (index < text.length) {
            val char = text[index]
            when {
                char == '"' && quoted && text.getOrNull(index + 1) == '"' -> {
                    value.append('"')
                    index++
                }
                char == '"' && quoted -> quoted = false
                char == '"' && value.isEmpty() -> quoted = true
                char == ',' && !quoted -> finishValue()
                char == '\r' && !quoted -> {
                    if (text.getOrNull(index + 1) == '\n') index++
                    finishRecord()
                }
                char == '\n' && !quoted -> finishRecord()
                else -> value.append(char)
            }
            index++
        }
        require(!quoted) { "CSV file contains an unterminated quoted value" }
        if (value.isNotEmpty() || record.isNotEmpty()) finishRecord()
        return records
    }

    private fun String.csvEscape(): String =
        if (any { it == ',' || it == '"' || it == '\r' || it == '\n' }) {
            "\"${replace("\"", "\"\"")}\""
        } else {
            this
        }

    private fun String.headerKey(): String =
        removePrefix("\uFEFF")
            .trim()
            .lowercase()
            .replace(Regex("[\\s-]+"), "_")

    private fun String?.semicolonList(): List<String> =
        orEmpty().split(';').map(String::trim).filter(String::isNotBlank)

    private fun String?.toCsvBoolean(): Boolean =
        this?.trim()?.lowercase() in setOf("true", "yes", "y", "1")

    private fun Long?.toCsvDate(): String =
        this?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString() }.orEmpty()

    private fun String?.toEpochMillisOrNull(): Long? {
        val value = this?.trim()?.takeIf(String::isNotBlank) ?: return null
        return value.toLongOrNull() ?: runCatching {
            LocalDate.parse(value).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }.getOrNull()
    }
}
