package com.mj.homelibrary.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Rect
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

    private const val CDN_BEST_URL = "https://raw.githubusercontent.com/tesseract-ocr/tessdata_best/main/"
    private const val CDN_FALLBACK_URL = "https://raw.githubusercontent.com/tesseract-ocr/tessdata/main/"

    val availableLanguages = listOf(
        OcrLanguagePack("mal", "Malayalam", "മലയാളം", 4.7, "ml"),
        OcrLanguagePack("tam", "Tamil", "தமிழ்", 3.8, "ta"),
        OcrLanguagePack("hin", "Hindi", "हिन्दी", 4.2, "hi"),
        OcrLanguagePack("tel", "Telugu", "తెలుగు", 4.4, "te"),
        OcrLanguagePack("kan", "Kannada", "ಕನ್ನಡ", 4.1, "kn"),
        OcrLanguagePack("ben", "Bengali", "বাংলা", 4.0, "bn"),
        OcrLanguagePack("mar", "Marathi", "मराठी", 4.1, "mr"),
        OcrLanguagePack("guj", "Gujarati", "ગુજરાતી", 3.9, "gu"),
        OcrLanguagePack("san", "Sanskrit", "संस्कृतम्", 3.8, "sa"),
        OcrLanguagePack("pan", "Punjabi", "ਪੰਜਾਬੀ", 3.6, "pa"),
        OcrLanguagePack("ori", "Odia", "ଓଡ଼ିଆ", 3.7, "or"),
        OcrLanguagePack("asm", "Assamese", "অসমীয়া", 3.9, "as"),
        OcrLanguagePack("urd", "Urdu", "اردو", 3.5, "ur"),
        OcrLanguagePack("fra", "French", "Français", 3.2, "fr"),
        OcrLanguagePack("deu", "German", "Deutsch", 3.4, "de"),
        OcrLanguagePack("spa", "Spanish", "Español", 3.2, "es"),
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
        val minSize = when (code) {
            "mal" -> 3_000_000L
            "hin", "tel", "kan", "ben", "mar", "guj", "tam" -> 2_500_000L
            else -> 100_000L
        }
        return file.exists() && file.length() >= minSize
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

            var downloadSuccess = false
            for (baseUrl in listOf(CDN_BEST_URL, CDN_FALLBACK_URL)) {
                try {
                    val url = URL("$baseUrl$code.traineddata")
                    val connection = (url.openConnection() as HttpURLConnection).apply {
                        connectTimeout = 15_000
                        readTimeout = 40_000
                        instanceFollowRedirects = true
                    }
                    if (connection.responseCode in 200..299) {
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
                            downloadSuccess = true
                            break
                        }
                    }
                } catch (_: Exception) {
                    tempFile.delete()
                }
            }

            if (downloadSuccess && tempFile.exists() && tempFile.length() > 50_000L) {
                if (targetFile.exists()) targetFile.delete()
                tempFile.renameTo(targetFile)
            } else {
                tempFile.delete()
                throw IllegalStateException("Failed to download traineddata model for $code")
            }
            Unit
        }
    }

    fun deleteLanguagePack(context: Context, code: String): Boolean {
        val file = File(getTessDataDir(context), "$code.traineddata")
        return if (file.exists()) file.delete() else false
    }

    fun preprocessBitmapForOcr(src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height

        val maxDim = maxOf(width, height)
        val scale = if (maxDim > 1800) 1800f / maxDim else if (maxDim < 900) 1400f / maxDim else 1.0f
        val scaledWidth = (width * scale).toInt()
        val scaledHeight = (height * scale).toInt()

        val dest = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(dest)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // Convert to high-contrast grayscale for crisp font edges
        val colorMatrix = ColorMatrix().apply {
            setSaturation(0f)
            val contrast = 1.4f
            val translate = (-0.5f * contrast + 0.5f) * 255f
            val contrastMatrix = ColorMatrix(
                floatArrayOf(
                    contrast, 0f, 0f, 0f, translate,
                    0f, contrast, 0f, 0f, translate,
                    0f, 0f, contrast, 0f, translate,
                    0f, 0f, 0f, 1f, 0f,
                )
            )
            postConcat(contrastMatrix)
        }
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        val srcRect = Rect(0, 0, width, height)
        val destRect = Rect(0, 0, scaledWidth, scaledHeight)
        canvas.drawBitmap(src, srcRect, destRect, paint)
        return dest
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

            tessBaseApi.pageSegMode = TessBaseAPI.PageSegMode.PSM_AUTO
            tessBaseApi.setVariable("preserve_interword_spaces", "1")
            tessBaseApi.setVariable("tessedit_enable_doc_dict", "0")

            val processed = preprocessBitmapForOcr(bitmap)
            tessBaseApi.setImage(processed)
            var utf8Text = tessBaseApi.utF8Text.orEmpty()

            if (utf8Text.length < 5) {
                tessBaseApi.setImage(bitmap)
                val fallbackText = tessBaseApi.utF8Text.orEmpty()
                if (fallbackText.length > utf8Text.length) {
                    utf8Text = fallbackText
                }
            }

            val rawLines = utf8Text.lines()
                .map { cleanOcrLine(it) }
                .filter { it.length >= 2 && !it.matches(Regex("^[0-9\\-\\s]+$")) }

            if (rawLines.isEmpty()) {
                return@withContext OcrBookDetails()
            }

            val title = rawLines.firstOrNull().orEmpty()
            val authors = if (rawLines.size > 1) listOf(rawLines[1]) else emptyList()
            val publisher = if (rawLines.size > 2) rawLines.lastOrNull() else null

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

    private fun cleanOcrLine(input: String): String {
        return input.trim()
            .trim('"', '“', '”', '‘', '’', '\'', '-', '_', '~', '|', '*', '•', ':', ';', ',', '.', '/', '\\', '<', '>', '(', ')')
            .replace(Regex("""\s+"""), " ")
    }
}
