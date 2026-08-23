package com.mj.homelibrary.data.remote

import android.content.Context
import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class OcrLanguagePack(
    val code: String,
    val englishName: String,
    val nativeName: String,
    val sizeMb: Double,
    val bookLanguageCode: String,
)

object OcrLanguageManager {

    private const val CDN_BASE_URL = "https://raw.githubusercontent.com/tesseract-ocr/tessdata_fast/main/"

    val availableLanguages = listOf(
        OcrLanguagePack("mal", "Malayalam", "മലയാളം", 2.4, "ml"),
        OcrLanguagePack("tam", "Tamil", "தமிழ்", 1.6, "ta"),
        OcrLanguagePack("hin", "Hindi", "हिन्दी", 1.8, "hi"),
        OcrLanguagePack("tel", "Telugu", "తెలుగు", 2.1, "te"),
        OcrLanguagePack("kan", "Kannada", "ಕನ್ನಡ", 2.0, "kn"),
        OcrLanguagePack("ben", "Bengali", "বাংলা", 1.8, "bn"),
        OcrLanguagePack("mar", "Marathi", "मराठी", 1.8, "mr"),
        OcrLanguagePack("guj", "Gujarati", "ગુજરાતી", 1.6, "gu"),
        OcrLanguagePack("san", "Sanskrit", "संस्कृतम्", 1.7, "sa"),
        OcrLanguagePack("pan", "Punjabi", "ਪੰਜਾਬੀ", 1.5, "pa"),
        OcrLanguagePack("ori", "Odia", "ଓଡ଼ିଆ", 1.6, "or"),
        OcrLanguagePack("asm", "Assamese", "অসমীয়া", 1.7, "as"),
        OcrLanguagePack("urd", "Urdu", "اردو", 1.9, "ur"),
        OcrLanguagePack("fra", "French", "Français", 1.4, "fr"),
        OcrLanguagePack("deu", "German", "Deutsch", 1.4, "de"),
        OcrLanguagePack("spa", "Spanish", "Español", 1.4, "es"),
    )

    fun getTessDataDir(context: Context): File {
        val dir = File(context.filesDir, "tessdata")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun isLanguageInstalled(context: Context, code: String): Boolean {
        if (code == "eng" || code == "latin") return true
        val file = File(getTessDataDir(context), "$code.traineddata")
        return file.exists() && file.length() > 50_000L
    }

    fun getInstalledLanguages(context: Context): List<OcrLanguagePack> {
        return availableLanguages.filter { isLanguageInstalled(context, it.code) }
    }

    suspend fun downloadLanguagePack(
        context: Context,
        code: String,
        onProgress: (Float) -> Unit = {},
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val tessDir = getTessDataDir(context)
            val tempFile = File(tessDir, "$code.traineddata.tmp")
            val targetFile = File(tessDir, "$code.traineddata")

            val url = URL("$CDN_BASE_URL$code.traineddata")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 15_000
                readTimeout = 30_000
                instanceFollowRedirects = true
            }

            val totalBytes = connection.contentLength
            connection.inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalRead = 0L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        if (totalBytes > 0) {
                            onProgress((totalRead.toFloat() / totalBytes).coerceIn(0f, 1f))
                        }
                    }
                }
            }

            if (tempFile.exists() && tempFile.length() > 50_000L) {
                if (targetFile.exists()) targetFile.delete()
                tempFile.renameTo(targetFile)
            } else {
                tempFile.delete()
                throw IllegalStateException("Downloaded file is invalid or too small.")
            }
            Unit
        }
    }

    fun deleteLanguagePack(context: Context, code: String): Boolean {
        val file = File(getTessDataDir(context), "$code.traineddata")
        return if (file.exists()) file.delete() else false
    }

    suspend fun recognizeBitmap(
        context: Context,
        bitmap: Bitmap,
        langCode: String,
    ): OcrBookDetails = withContext(Dispatchers.Default) {
        if (!isLanguageInstalled(context, langCode)) {
            return@withContext OcrBookDetails()
        }

        val tessBaseApi = TessBaseAPI()
        try {
            val dataPath = context.filesDir.absolutePath
            val success = tessBaseApi.init(dataPath, langCode)
            if (!success) {
                return@withContext OcrBookDetails()
            }

            tessBaseApi.setImage(bitmap)
            val utf8Text = tessBaseApi.utF8Text.orEmpty()
            val rawLines = utf8Text.lines()
                .map { it.trim().trim('"', '“', '”', '\'', '-', '_', '~', '|', '*', '•', ':', ';', ',', '.') }
                .filter { it.length >= 2 }

            if (rawLines.isEmpty()) {
                return@withContext OcrBookDetails()
            }

            val title = rawLines.firstOrNull().orEmpty()
            val authors = if (rawLines.size > 1) listOf(rawLines[1]) else emptyList()
            val publisher = if (rawLines.size > 2) rawLines.lastOrNull() else null

            val pack = availableLanguages.firstOrNull { it.code == langCode }
            OcrBookDetails(
                title = title,
                authors = authors,
                publisher = publisher,
                allDetectedLines = rawLines,
            )
        } catch (e: Exception) {
            OcrBookDetails()
        } finally {
            tessBaseApi.recycle()
        }
    }
}
