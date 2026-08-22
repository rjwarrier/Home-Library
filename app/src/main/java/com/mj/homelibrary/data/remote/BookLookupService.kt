package com.mj.homelibrary.data.remote

import android.content.Context
import android.text.Html
import com.mj.homelibrary.data.normalizedIsbn
import com.mj.homelibrary.data.normalizedIsbn10OrNull
import com.mj.homelibrary.data.normalizedIsbn13OrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import com.mj.homelibrary.data.BoundedLruCache
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class BookLookupService(private val context: Context) {
    private val metadataCache = BoundedLruCache<String, CacheEntry<BookMetadata>>(METADATA_CACHE_MAX_ENTRIES)
    private val coverCandidateCache = BoundedLruCache<String, CacheEntry<List<String>>>(COVER_CANDIDATE_CACHE_MAX_ENTRIES)

    suspend fun lookup(isbn: String): Result<BookMetadata> = withContext(Dispatchers.IO) {
        runCatching {
            val normalized = isbn.normalizedIsbn()
            if (normalized.isBlank()) error("ISBN is blank")
            metadataCache.freshValue(normalized)?.let { return@runCatching it }
            val metadata = coroutineScope {
                val openLibrary = async { runCatching { lookupOpenLibrary(normalized) }.getOrNull() }
                val googleBooks = async { runCatching { lookupGoogleBooks(normalized) }.getOrNull() }
                openLibrary.await().mergeWith(googleBooks.await())
            } ?: error("No metadata found")
            metadataCache.put(normalized, CacheEntry(metadata))
            metadata
        }
    }

    suspend fun searchCoverCandidates(isbn: String, title: String, authors: List<String>): List<String> =
        withContext(Dispatchers.IO) {
            val normalized = isbn.normalizedIsbn()
            val cacheKey = listOf(normalized, title.trim().lowercase(), authors.firstOrNull().orEmpty().trim().lowercase())
                .joinToString("|")
            coverCandidateCache.freshValue(cacheKey)?.let { return@withContext it }

            val candidateGroups = coroutineScope {
                val directOpenLibrary = async {
                    if (normalized.isBlank()) return@async emptyList()
                    runCatching {
                        getJson("https://openlibrary.org/isbn/$normalized.json")
                            ?.optJSONArray("covers")
                            ?.let { array -> List(array.length()) { array.optLong(it) } }
                            ?.filter { it > 0L }
                            ?.map { "https://covers.openlibrary.org/b/id/$it-L.jpg" }
                            .orEmpty()
                    }.getOrDefault(emptyList())
                }
                val openLibrarySearch = async {
                    if (normalized.isBlank()) return@async emptyList()
                    runCatching {
                        val encoded = URLEncoder.encode(normalized, StandardCharsets.UTF_8.name())
                        getJson("https://openlibrary.org/search.json?isbn=$encoded&fields=cover_i")
                            ?.optJSONArray("docs")
                            ?.let { array -> List(array.length()) { array.optJSONObject(it) } }
                            ?.mapNotNull { it?.optLong("cover_i")?.takeIf { id -> id > 0L } }
                            ?.map { "https://covers.openlibrary.org/b/id/$it-L.jpg" }
                            .orEmpty()
                    }.getOrDefault(emptyList())
                }
                val googleIsbnSearch = async {
                    if (normalized.isBlank()) return@async emptyList()
                    runCatching {
                        val encoded = URLEncoder.encode("isbn:$normalized", StandardCharsets.UTF_8.name())
                        googleCoverUrls("https://www.googleapis.com/books/v1/volumes?q=$encoded")
                    }.getOrDefault(emptyList())
                }
                val googleTitleSearch = async {
                    if (title.isBlank()) return@async emptyList()
                    runCatching {
                        val query = buildString {
                            append("intitle:")
                            append(title)
                            authors.firstOrNull()?.let { append("+inauthor:"); append(it) }
                        }
                        val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name())
                        googleCoverUrls(
                            "https://www.googleapis.com/books/v1/volumes?q=$encoded&maxResults=$MAX_TITLE_SEARCH_RESULTS",
                        )
                    }.getOrDefault(emptyList())
                }
                listOf(directOpenLibrary, openLibrarySearch, googleIsbnSearch, googleTitleSearch).awaitAll()
            }
            val candidates = candidateGroups.flatten().distinct().take(MAX_COVER_CANDIDATES)
            coverCandidateCache.put(cacheKey, CacheEntry(candidates))
            candidates
        }

    private suspend fun lookupOpenLibrary(isbn: String): BookMetadata? {
        val url = "https://openlibrary.org/isbn/$isbn.json"
        val json = getJson(url) ?: return null
        val title = json.optString("title").takeIf(String::isNotBlank) ?: return null
        val work = json.optJSONArray("works")
            ?.optJSONObject(0)
            ?.optString("key")
            ?.takeIf(String::isNotBlank)
            ?.let { getJson("https://openlibrary.org$it.json") }
        val authorKeys = json.optJSONArray("authors")?.let { authorArray ->
            List(authorArray.length()) { index -> authorArray.optJSONObject(index)?.optString("key").orEmpty() }
                .filter(String::isNotBlank)
                .distinct()
        }.orEmpty()
        val authors = coroutineScope {
            authorKeys.map { key -> async { openLibraryAuthorName(key) } }.awaitAll()
        }.filter(String::isNotBlank).distinct()
        val publishers = json.optJSONArray("publishers").toStringList()
        val publishDate = json.optString("publish_date")
        val coverId = json.optJSONArray("covers")?.optLong(0)?.takeIf { it > 0L }
        val tags = (
            work?.optJSONArray("subjects").toStringList() +
                work?.optJSONArray("subject_places").toStringList() +
                work?.optJSONArray("subject_times").toStringList()
            ).map { it.trim() }
            .filter(String::isNotBlank)
            .distinct()
            .take(MAX_TAGS)
        val languageCode = json.optJSONArray("languages")
            ?.optJSONObject(0)
            ?.optString("key")
            ?.substringAfterLast("/")
            ?.toAppLanguageCode()
            ?: "en"

        return BookMetadata(
            title = title,
            subtitle = json.optString("subtitle").takeIf(String::isNotBlank),
            authors = authors,
            tags = tags,
            publisher = publishers.firstOrNull(),
            publishedYear = publishDate.toYearOrNull(),
            pageCount = json.optInt("number_of_pages").takeIf { it > 0 },
            coverUrl = coverId?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" },
            languageCode = languageCode,
            isbn10 = json.optJSONArray("isbn_10").toStringList().firstNotNullOfOrNull {
                it.normalizedIsbn10OrNull()
            }
                ?: isbn.normalizedIsbn10OrNull(),
            isbn13 = json.optJSONArray("isbn_13").toStringList().firstNotNullOfOrNull {
                it.normalizedIsbn13OrNull()
            }
                ?: isbn.normalizedIsbn13OrNull(),
            formatCode = json.optString("physical_format").toFormatCodeOrNull(),
            notes = (work?.optDescription() ?: json.optDescription()).toSynopsis(),
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
            tags = item.optJSONArray("categories").toStringList(),
            publisher = item.optString("publisher").takeIf(String::isNotBlank),
            publishedYear = item.optString("publishedDate").toYearOrNull(),
            pageCount = item.optInt("pageCount").takeIf { it > 0 },
            coverUrl = item.optJSONObject("imageLinks").bestGoogleCoverUrl(),
            languageCode = item.optString("language").takeIf(String::isNotBlank) ?: "en",
            isbn10 = isbn10?.normalizedIsbn10OrNull() ?: isbn.normalizedIsbn10OrNull(),
            isbn13 = isbn13?.normalizedIsbn13OrNull() ?: isbn.normalizedIsbn13OrNull(),
            notes = item.optString("description").takeIf(String::isNotBlank).toSynopsis(),
        )
    }

    private fun openLibraryAuthorName(key: String): String {
        if (key.isBlank()) return ""
        val author = runCatching { getJson("https://openlibrary.org$key.json") }.getOrNull()
        return author?.optString("name")?.takeIf(String::isNotBlank)
            ?: key.substringAfterLast("/")
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

    private fun googleCoverUrls(url: String): List<String> =
        getJson(url)
            ?.optJSONArray("items")
            ?.let { array -> List(array.length()) { array.optJSONObject(it) } }
            ?.mapNotNull { it?.optJSONObject("volumeInfo")?.optJSONObject("imageLinks").bestGoogleCoverUrl() }
            .orEmpty()

    private fun <T> BoundedLruCache<String, CacheEntry<T>>.freshValue(key: String): T? {
        val entry = get(key) ?: return null
        if (entry.expiresAtEpochMillis > System.currentTimeMillis()) return entry.value
        remove(key)
        return null
    }

    private fun BookMetadata?.mergeWith(fallback: BookMetadata?): BookMetadata? {
        if (this == null) return fallback
        if (fallback == null) return this
        return BookMetadata(
            title = title.ifBlank { fallback.title },
            subtitle = subtitle ?: fallback.subtitle,
            authors = (authors + fallback.authors).distinctBy { it.lowercase() },
            tags = (tags + fallback.tags).distinctBy { it.lowercase() }.take(MAX_TAGS),
            publisher = publisher ?: fallback.publisher,
            publishedYear = publishedYear ?: fallback.publishedYear,
            pageCount = pageCount ?: fallback.pageCount,
            coverUrl = coverUrl ?: fallback.coverUrl,
            languageCode = languageCode.takeIf { it.isNotBlank() && it != "other" } ?: fallback.languageCode,
            isbn10 = isbn10 ?: fallback.isbn10,
            isbn13 = isbn13 ?: fallback.isbn13,
            formatCode = formatCode ?: fallback.formatCode,
            notes = bestSynopsis(notes, fallback.notes),
        )
    }

    private fun bestSynopsis(primary: String?, fallback: String?): String? =
        listOfNotNull(primary, fallback)
            .mapNotNull { it.toSynopsis() }
            .distinctBy { it.lowercase() }
            .maxByOrNull { it.length }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return List(length()) { index -> optString(index) }
            .filter(String::isNotBlank)
    }

    private fun JSONObject?.bestGoogleCoverUrl(): String? {
        if (this == null) return null
        return listOf("extraLarge", "large", "medium", "small", "thumbnail", "smallThumbnail")
            .firstNotNullOfOrNull { key -> optString(key).takeIf(String::isNotBlank) }
            ?.replace("http://", "https://")
    }

    private fun JSONObject.optDescription(): String? {
        val value = opt("description") ?: return null
        return when (value) {
            is String -> value
            is JSONObject -> value.optString("value")
            else -> null
        }?.takeIf(String::isNotBlank)
    }

    private fun String?.toSynopsis(): String? {
        val value = this?.takeIf(String::isNotBlank) ?: return null
        return Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY)
            .toString()
            .lineSequence()
            .map { it.trim() }
            .filter(String::isNotBlank)
            .joinToString("\n")
            .takeIf(String::isNotBlank)
    }

    private fun String?.toFormatCodeOrNull(): String? {
        val value = this?.lowercase().orEmpty()
        return when {
            "hardcover" in value || "hardback" in value -> "hardcover"
            "paperback" in value || "paper back" in value -> "paperback"
            "ebook" in value || "e-book" in value || "electronic" in value -> "ebook"
            value.isBlank() -> null
            else -> "other"
        }
    }

    private fun String.toAppLanguageCode(): String = when (lowercase()) {
        "eng", "en" -> "en"
        "mal", "ml" -> "ml"
        "hin", "hi" -> "hi"
        else -> "other"
    }

    private fun String?.toYearOrNull(): Int? = this?.let {
        Regex("""\d{4}""").find(it)?.value?.toIntOrNull()
    }

    private companion object {
        const val NETWORK_TIMEOUT_MS = 12_000
        const val MAX_TAGS = 12
        const val MAX_COVER_CANDIDATES = 8
        const val MAX_TITLE_SEARCH_RESULTS = 4
        const val CACHE_TTL_MILLIS = 6 * 60 * 60 * 1000L
        const val METADATA_CACHE_MAX_ENTRIES = 300
        const val COVER_CANDIDATE_CACHE_MAX_ENTRIES = 300
    }

    private data class CacheEntry<T>(
        val value: T,
        val expiresAtEpochMillis: Long = System.currentTimeMillis() + CACHE_TTL_MILLIS,
    )
}
