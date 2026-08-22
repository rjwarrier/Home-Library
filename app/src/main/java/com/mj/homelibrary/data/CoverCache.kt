package com.mj.homelibrary.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

class CoverCache(private val context: Context) {
    private val memoryCache = BoundedLruCache<String, String>(MEMORY_CACHE_MAX_ENTRIES)

    suspend fun cache(url: String?): String? = withContext(Dispatchers.IO) {
        if (url.isNullOrBlank()) return@withContext null
        memoryCache.get(url)?.let { cachedPath ->
            if (File(cachedPath).exists()) return@withContext cachedPath
            memoryCache.remove(url)
        }

        runCatching {
            val directory = File(context.filesDir, COVER_DIRECTORY).also { it.mkdirs() }
            val file = File(directory, "${url.sha256()}.jpg")
            if (file.exists() && file.length() > 0L) {
                val path = file.absolutePath
                memoryCache.put(url, path)
                return@runCatching path
            }
            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = NETWORK_TIMEOUT_MS
                readTimeout = NETWORK_TIMEOUT_MS
            }
            try {
                if (connection.responseCode !in 200..299) return@runCatching null
                connection.inputStream.use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
                val path = file.absolutePath
                memoryCache.put(url, path)
                path
            } finally {
                connection.disconnect()
            }
        }.getOrNull()
    }

    private fun String.sha256(): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private companion object {
        const val COVER_DIRECTORY = "covers"
        const val NETWORK_TIMEOUT_MS = 12_000
        const val MEMORY_CACHE_MAX_ENTRIES = 200
    }
}
