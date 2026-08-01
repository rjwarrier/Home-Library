package com.mj.homelibrary.data.remote

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class BookLookupService(private val context: Context) {
    suspend fun lookup(isbn: String): Result<BookMetadata> = withContext(Dispatchers.IO) {
        val normalized = isbn.filter(Char::isDigit)
        runCatching {
            lookupOpenLibrary(normalized) ?: lookupGoogleBooks(normalized)
                ?: error("No metadata found")
        }
    }

    private fun lookupOpenLibrary(isbn: String): BookMetadata? {
        val url = "https://openlibrary.org/isbn/$isbn.json"
        val json = getJson(url) ?: return null
        val title = json.optString("title").takeIf(String::isNotBlank) ?: return null
        val authors = json.optJSONArray("authors")?.let { authorArray ->
            List(authorArray.length()) { index ->
                authorArray.optJSONObject(index)?.optString("key").orEmpty().substringAfterLast("/")
            }.filter(String::isNotBlank)
        }.orEmpty()
        val publishers = json.optJSONArray("publishers")
        val publishDate = json.optString("publish_date")
        val coverId = json.optJSONArray("covers")?.optLong(0)?.takeIf { it > 0L }

        return BookMetadata(
            title = title,
            authors = authors,
            publisher = publishers?.optString(0),
            publishedYear = publishDate.toYearOrNull(),
            pageCount = json.optInt("number_of_pages").takeIf { it > 0 },
            coverUrl = coverId?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" },
            isbn10 = isbn.takeIf { it.length == 10 },
            isbn13 = isbn.takeIf { it.length == 13 },
        )
    }

    private fun lookupGoogleBooks(isbn: String): BookMetadata? {
        val encoded = URLEncoder.encode("isbn:$isbn", StandardCharsets.UTF_8.name())
        val json = getJson("https://www.googleapis.com/books/v1/volumes?q=$encoded") ?: return null
        val item = json.optJSONArray("items")?.optJSONObject(0)?.optJSONObject("volumeInfo") ?: return null
        val title = item.optString("title").takeIf(String::isNotBlank) ?: return null
        val industryIds = item.optJSONArray("industryIdentifiers")
        var isbn10: String? = null
        var isbn13: String? = null
        if (industryIds != null) {
            for (index in 0 until industryIds.length()) {
                val identifier = industryIds.optJSONObject(index) ?: continue
                when (identifier.optString("type")) {
                    "ISBN_10" -> isbn10 = identifier.optString("identifier")
                    "ISBN_13" -> isbn13 = identifier.optString("identifier")
                }
            }
        }

        return BookMetadata(
            title = title,
            subtitle = item.optString("subtitle").takeIf(String::isNotBlank),
            authors = item.optJSONArray("authors")?.let { array ->
                List(array.length()) { index -> array.optString(index) }.filter(String::isNotBlank)
            }.orEmpty(),
            publisher = item.optString("publisher").takeIf(String::isNotBlank),
            publishedYear = item.optString("publishedDate").toYearOrNull(),
            pageCount = item.optInt("pageCount").takeIf { it > 0 },
            coverUrl = item.optJSONObject("imageLinks")?.optString("thumbnail")?.replace("http://", "https://"),
            languageCode = item.optString("language").takeIf(String::isNotBlank) ?: "en",
            isbn10 = isbn10 ?: isbn.takeIf { it.length == 10 },
            isbn13 = isbn13 ?: isbn.takeIf { it.length == 13 },
        )
    }

    private fun getJson(url: String): JSONObject? {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = NETWORK_TIMEOUT_MS
            readTimeout = NETWORK_TIMEOUT_MS
            requestMethod = "GET"
            setRequestProperty("User-Agent", "${context.packageName}/1.0")
        }
        return try {
            if (connection.responseCode !in 200..299) return null
            JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
        } finally {
            connection.disconnect()
        }
    }

    private fun String?.toYearOrNull(): Int? = this?.let {
        Regex("""\d{4}""").find(it)?.value?.toIntOrNull()
    }

    private companion object {
        const val NETWORK_TIMEOUT_MS = 12_000
    }
}
