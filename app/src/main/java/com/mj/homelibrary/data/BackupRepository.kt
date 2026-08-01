package com.mj.homelibrary.data

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.mj.homelibrary.data.entity.BookEntity
import com.mj.homelibrary.data.entity.LoanEntity
import com.mj.homelibrary.data.entity.LocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class BackupRepository(
    private val context: Context,
    private val database: AppDatabase,
) {
    suspend fun exportJson(uri: Uri) = withContext(Dispatchers.IO) {
        val snapshot = JSONObject()
            .put(KEY_SCHEMA_VERSION, SCHEMA_VERSION)
            .put(KEY_EXPORTED_AT, System.currentTimeMillis())
            .put(KEY_LOCATIONS, JSONArray(database.locationDao().getAll().map { it.toJson() }))
            .put(KEY_BOOKS, JSONArray(database.bookDao().getAll().map { it.toJson() }))
            .put(KEY_LOANS, JSONArray(database.loanDao().getAll().map { it.toJson() }))
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.writer().use { it.write(snapshot.toString(JSON_INDENT_SPACES)) }
        }
    }

    suspend fun exportCsv(uri: Uri) = withContext(Dispatchers.IO) {
        val books = database.bookDao().getAll()
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.writer().use { writer ->
                writer.appendLine(CSV_HEADER)
                books.forEach { book ->
                    writer.appendLine(
                        listOf(
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
                        ).joinToString(",") { it.csvEscape() },
                    )
                }
            }
        }
    }

    suspend fun importJson(uri: Uri) = withContext(Dispatchers.IO) {
        val text = context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.bufferedReader().use { it.readText() }
        } ?: return@withContext
        val snapshot = JSONObject(text)
        val locations = snapshot.optJSONArray(KEY_LOCATIONS).toLocationList()
        val books = snapshot.optJSONArray(KEY_BOOKS).toBookList()
        val loans = snapshot.optJSONArray(KEY_LOANS).toLoanList()
        database.withTransaction {
            database.loanDao().clear()
            database.bookDao().clear()
            database.locationDao().clear()
            database.locationDao().insertAll(locations)
            database.bookDao().insertAll(books)
            database.loanDao().insertAll(loans)
        }
    }

    private fun BookEntity.toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("title", title)
        .put("subtitle", subtitle)
        .put("authors", JSONArray(authors))
        .put("languageCode", languageCode)
        .put("originalScriptTitle", originalScriptTitle)
        .put("tags", JSONArray(tags))
        .put("isbn10", isbn10)
        .put("isbn13", isbn13)
        .put("publisher", publisher)
        .put("publishedYear", publishedYear)
        .put("pageCount", pageCount)
        .put("coverImagePath", coverImagePath)
        .put("coverUrl", coverUrl)
        .put("formatCode", formatCode)
        .put("acquisitionDateEpochMillis", acquisitionDateEpochMillis)
        .put("notes", notes)
        .put("rating", rating)
        .put("readStatusCode", readStatusCode)
        .put("locationId", locationId)
        .put("positionNote", positionNote)
        .put("addedDateEpochMillis", addedDateEpochMillis)

    private fun LocationEntity.toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("room", room)
        .put("unit", unit)
        .put("shelf", shelf)
        .put("sortOrder", sortOrder)

    private fun LoanEntity.toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("bookId", bookId)
        .put("borrowerName", borrowerName)
        .put("borrowerContact", borrowerContact)
        .put("loanDateEpochMillis", loanDateEpochMillis)
        .put("expectedReturnDateEpochMillis", expectedReturnDateEpochMillis)
        .put("actualReturnDateEpochMillis", actualReturnDateEpochMillis)
        .put("notes", notes)

    private fun JSONArray?.toBookList(): List<BookEntity> {
        if (this == null) return emptyList()
        return List(length()) { index -> optJSONObject(index) }.filterNotNull().map { json ->
            BookEntity(
                id = json.optLong("id"),
                title = json.optString("title"),
                subtitle = json.optNullableString("subtitle"),
                authors = json.optStringArray("authors"),
                languageCode = json.optString("languageCode", "en"),
                originalScriptTitle = json.optNullableString("originalScriptTitle"),
                tags = json.optStringArray("tags"),
                isbn10 = json.optNullableString("isbn10"),
                isbn13 = json.optNullableString("isbn13"),
                publisher = json.optNullableString("publisher"),
                publishedYear = json.optNullableInt("publishedYear"),
                pageCount = json.optNullableInt("pageCount"),
                coverImagePath = json.optNullableString("coverImagePath"),
                coverUrl = json.optNullableString("coverUrl"),
                formatCode = json.optString("formatCode", "paperback"),
                acquisitionDateEpochMillis = json.optNullableLong("acquisitionDateEpochMillis"),
                notes = json.optNullableString("notes"),
                rating = json.optNullableDouble("rating")?.toFloat(),
                readStatusCode = json.optString("readStatusCode", "unread"),
                locationId = json.optNullableLong("locationId"),
                positionNote = json.optNullableString("positionNote"),
                addedDateEpochMillis = json.optLong("addedDateEpochMillis", System.currentTimeMillis()),
            )
        }
    }

    private fun JSONArray?.toLocationList(): List<LocationEntity> {
        if (this == null) return emptyList()
        return List(length()) { index -> optJSONObject(index) }.filterNotNull().map { json ->
            LocationEntity(
                id = json.optLong("id"),
                room = json.optString("room"),
                unit = json.optString("unit"),
                shelf = json.optString("shelf"),
                sortOrder = json.optInt("sortOrder"),
            )
        }
    }

    private fun JSONArray?.toLoanList(): List<LoanEntity> {
        if (this == null) return emptyList()
        return List(length()) { index -> optJSONObject(index) }.filterNotNull().map { json ->
            LoanEntity(
                id = json.optLong("id"),
                bookId = json.optLong("bookId"),
                borrowerName = json.optString("borrowerName"),
                borrowerContact = json.optNullableString("borrowerContact"),
                loanDateEpochMillis = json.optLong("loanDateEpochMillis"),
                expectedReturnDateEpochMillis = json.optNullableLong("expectedReturnDateEpochMillis"),
                actualReturnDateEpochMillis = json.optNullableLong("actualReturnDateEpochMillis"),
                notes = json.optNullableString("notes"),
            )
        }
    }

    private fun JSONObject.optStringArray(key: String): List<String> {
        val array = optJSONArray(key) ?: return emptyList()
        return List(array.length()) { index -> array.optString(index) }.filter(String::isNotBlank)
    }

    private fun JSONObject.optNullableString(key: String): String? =
        if (isNull(key)) null else optString(key).takeIf(String::isNotBlank)

    private fun JSONObject.optNullableLong(key: String): Long? =
        if (isNull(key) || !has(key)) null else optLong(key)

    private fun JSONObject.optNullableInt(key: String): Int? =
        if (isNull(key) || !has(key)) null else optInt(key)

    private fun JSONObject.optNullableDouble(key: String): Double? =
        if (isNull(key) || !has(key)) null else optDouble(key)

    private fun String.csvEscape(): String = "\"${replace("\"", "\"\"")}\""

    private companion object {
        const val SCHEMA_VERSION = 1
        const val JSON_INDENT_SPACES = 2
        const val KEY_SCHEMA_VERSION = "schemaVersion"
        const val KEY_EXPORTED_AT = "exportedAtEpochMillis"
        const val KEY_BOOKS = "books"
        const val KEY_LOCATIONS = "locations"
        const val KEY_LOANS = "loans"
        const val CSV_HEADER = "title,authors,language,isbn13,isbn10,publisher,published_year,tags,read_status,rating"
    }
}
