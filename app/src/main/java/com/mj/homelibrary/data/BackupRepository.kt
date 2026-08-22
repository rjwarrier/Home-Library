package com.mj.homelibrary.data

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.room.withTransaction
import com.mj.homelibrary.data.entity.BookEntity
import com.mj.homelibrary.data.entity.BorrowerEntity
import com.mj.homelibrary.data.entity.LoanEntity
import com.mj.homelibrary.data.entity.LocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.io.IOException
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

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
            .put(KEY_BORROWERS, JSONArray(database.borrowerDao().getAll().map { it.toJson() }))
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.writer().use { it.write(snapshot.toString(JSON_INDENT_SPACES)) }
        }
    }

    suspend fun exportCompleteBackup(uri: Uri) = withContext(Dispatchers.IO) {
        val books = database.bookDao().getAll()
        val manifest = JSONObject()
            .put(KEY_SCHEMA_VERSION, SCHEMA_VERSION)
            .put(KEY_BACKUP_KIND, BACKUP_KIND_COMPLETE)
            .put(KEY_EXPORTED_AT, System.currentTimeMillis())
            .put(KEY_LOCATIONS, JSONArray(database.locationDao().getAll().map { it.toJson() }))
            .put(KEY_BOOKS, JSONArray(books.map { it.toJson() }))
            .put(KEY_LOANS, JSONArray(database.loanDao().getAll().map { it.toJson() }))
            .put(KEY_BORROWERS, JSONArray(database.borrowerDao().getAll().map { it.toJson() }))
        context.contentResolver.openOutputStream(uri)?.use { output ->
            ZipOutputStream(output).use { zip ->
                zip.putNextEntry(ZipEntry(MANIFEST_ENTRY_NAME))
                zip.write(manifest.toString(JSON_INDENT_SPACES).toByteArray(Charsets.UTF_8))
                zip.closeEntry()
                books.forEach { book ->
                    val coverInput = book.openCoverInputStream() ?: return@forEach
                    coverInput.use { input ->
                        zip.putNextEntry(ZipEntry("$COVER_ENTRY_DIR/book-${book.id}-cover.jpg"))
                        try {
                            input.copyTo(zip)
                        } finally {
                            zip.closeEntry()
                        }
                    }
                }
            }
        }
    }

    suspend fun exportCsv(uri: Uri) = withContext(Dispatchers.IO) {
        val books = database.bookDao().getAll()
        val locationsById = database.locationDao().getAll().associateBy { it.id }
        val rows = books.map { book ->
            val location = locationsById[book.locationId]
            BookCsvRow(
                book = book,
                room = location?.room,
                bookcase = location?.unit,
                shelf = location?.shelf,
            )
        }
        writeCsv(uri, BookCsvCodec.encode(rows))
    }

    suspend fun exportCsvTemplate(uri: Uri) = withContext(Dispatchers.IO) {
        writeCsv(uri, BookCsvCodec.template())
    }

    suspend fun exportHtmlCatalog(uri: Uri, targetBookIds: Set<Long>? = null) = withContext(Dispatchers.IO) {
        val allBooks = database.bookDao().getAll()
        val books = if (targetBookIds != null) allBooks.filter { it.id in targetBookIds } else allBooks
        val locationsById = database.locationDao().getAll().associateBy { it.id }

        val htmlContent = buildString {
            append("<!DOCTYPE html><html><head><meta charset='utf-8'>")
            append("<title>Home Library Catalog</title>")
            append("<style>")
            append("body { font-family: system-ui, -apple-system, sans-serif; margin: 0; padding: 24px; color: #1c1b1f; background: #fffbf2; }")
            append("h1 { color: #6f4e27; margin-bottom: 4px; }")
            append(".meta { color: #574539; font-size: 14px; margin-bottom: 24px; }")
            append(".grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; }")
            append(".card { background: #fff8ea; border: 1px solid #e9ddc7; border-radius: 12px; padding: 14px; display: flex; flex-direction: column; gap: 6px; }")
            append(".title { font-weight: 600; font-size: 16px; color: #201a16; }")
            append(".author { font-size: 13px; color: #574539; }")
            append(".badge { display: inline-block; padding: 3px 8px; border-radius: 6px; font-size: 11px; font-weight: 500; background: #ffdbe2; color: #3b0906; }")
            append(".badge.read { background: #e0f2fe; color: #0369a1; }")
            append(".location { font-size: 12px; color: #7b6c5d; margin-top: auto; padding-top: 8px; border-top: 1px dashed #d5c3ae; }")
            append("@media print { body { background: #fff; padding: 0; } .card { page-break-inside: avoid; } }")
            append("</style></head><body>")
            append("<h1>Home Library Catalog</h1>")
            append("<div class='meta'>Exported on ").append(java.time.LocalDate.now()).append(" &bull; ").append(books.size).append(" Books</div>")
            append("<div class='grid'>")
            books.forEach { book ->
                val loc = locationsById[book.locationId]
                val locString = if (loc != null) {
                    listOf(loc.room, loc.unit, loc.shelf).joinToString(" &rsaquo; ") { it.escapeHtml() }
                } else {
                    "Unassigned"
                }
                append("<div class='card'>")
                append("<div class='title'>").append(book.title.escapeHtml()).append("</div>")
                if (book.authors.isNotEmpty()) {
                    append("<div class='author'>").append(book.authors.joinToString(", ").escapeHtml()).append("</div>")
                }
                append("<div><span class='badge ")
                append(if (book.readStatusCode == ReadStatusCode.Finished.code) "read" else "")
                append("'>")
                append(book.readStatusCode.uppercase().escapeHtml()).append("</span></div>")
                if (!book.isbn13.isNullOrBlank() || !book.isbn10.isNullOrBlank()) {
                    append("<div style='font-size:11px;color:#7b6c5d;'>ISBN: ")
                    append((book.isbn13 ?: book.isbn10).orEmpty().escapeHtml()).append("</div>")
                }
                append("<div class='location'>📍 ").append(locString).append("</div>")
                append("</div>")
            }
            append("</div></body></html>")
        }

        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.writer(Charsets.UTF_8).use { it.write(htmlContent) }
        }
    }

    suspend fun exportPdfCatalog(uri: Uri, targetBookIds: Set<Long>? = null) = withContext(Dispatchers.IO) {
        val allBooks = database.bookDao().getAll()
        val books = if (targetBookIds != null) allBooks.filter { it.id in targetBookIds } else allBooks
        val locationsById = database.locationDao().getAll().associateBy { it.id }

        val pdfDocument = android.graphics.pdf.PdfDocument()
        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var pageNumber = 1

        val paintTitle = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(111, 78, 39)
            textSize = 20f
            isFakeBoldText = true
        }
        val paintHeader = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(32, 26, 22)
            textSize = 11f
            isFakeBoldText = true
        }
        val paintText = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(87, 69, 57)
            textSize = 10f
        }
        val paintLine = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(233, 221, 199)
            strokeWidth = 1f
        }

        var y = 40f
        canvas.drawText("Home Library Catalog", 36f, y, paintTitle)
        y += 18f
        canvas.drawText("Exported on ${java.time.LocalDate.now()} - Total ${books.size} Books", 36f, y, paintText)
        y += 24f

        fun drawColumnHeaders() {
            canvas.drawText("Title", 36f, y, paintHeader)
            canvas.drawText("Author", 220f, y, paintHeader)
            canvas.drawText("Status", 390f, y, paintHeader)
            canvas.drawText("Location", 460f, y, paintHeader)
            y += 8f
            canvas.drawLine(36f, y, 559f, y, paintLine)
            y += 16f
        }

        try {
            drawColumnHeaders()

            books.forEach { book ->
                if (y > 790f) {
                    pdfDocument.finishPage(page)
                    pageNumber++
                    val newPageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                    page = pdfDocument.startPage(newPageInfo)
                    canvas = page.canvas
                    y = 40f
                    drawColumnHeaders()
                }
                val loc = locationsById[book.locationId]
                val locStr = if (loc != null) "${loc.room}/${loc.unit}/${loc.shelf}" else "-"

                val authors = book.authors.joinToString()
                val titleTrimmed = if (book.title.length > 32) book.title.take(30) + "…" else book.title
                val authorTrimmed = if (authors.length > 24) authors.take(22) + "…" else authors

                canvas.drawText(titleTrimmed, 36f, y, paintHeader)
                canvas.drawText(authorTrimmed, 220f, y, paintText)
                canvas.drawText(book.readStatusCode.uppercase(), 390f, y, paintText)
                canvas.drawText(locStr, 460f, y, paintText)
                y += 18f
            }

            pdfDocument.finishPage(page)
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                pdfDocument.writeTo(stream)
            }
        } finally {
            pdfDocument.close()
        }
    }

    private fun String.escapeHtml(): String =
        replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")

    suspend fun importJson(uri: Uri) = withContext(Dispatchers.IO) {
        val text = context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.readUtf8TextLimited(MAX_IMPORT_TEXT_CHARS)
        } ?: return@withContext
        val snapshot = JSONObject(text)
        val locations = snapshot.optJSONArray(KEY_LOCATIONS).toLocationList()
        val books = snapshot.optJSONArray(KEY_BOOKS).toBookList()
        val loans = snapshot.optJSONArray(KEY_LOANS).toLoanList()
        val borrowers = snapshot.optJSONArray(KEY_BORROWERS).toBorrowerList()
        database.withTransaction {
            database.loanDao().clear()
            database.bookDao().clear()
            database.locationDao().clear()
            database.borrowerDao().clear()
            database.locationDao().insertAll(locations)
            database.bookDao().insertAll(books)
            database.borrowerDao().insertAll(borrowers)
            database.loanDao().insertAll(loans)
        }
    }

    suspend fun importCompleteBackup(uri: Uri) = withContext(Dispatchers.IO) {
        val isZip = context.contentResolver.openInputStream(uri)?.use { it.looksLikeZip() } ?: return@withContext
        if (isZip) importCompleteBackupZip(uri) else importCompleteBackupLegacyJson(uri)
    }

    private suspend fun importCompleteBackupZip(uri: Uri) {
        val directory = File(context.filesDir, COVER_DIRECTORY).also { it.mkdirs() }
        var manifest: JSONObject? = null
        val restoredCoverPaths = mutableMapOf<Long, String>()
        context.contentResolver.openInputStream(uri)?.use { input ->
            ZipInputStream(input).use { zip ->
                var entry = zip.getNextEntry()
                while (entry != null) {
                    when {
                        entry.name == MANIFEST_ENTRY_NAME -> {
                            manifest = JSONObject(zip.readUtf8TextLimited(MAX_MANIFEST_CHARS))
                        }
                        !entry.isDirectory && entry.name.startsWith("$COVER_ENTRY_DIR/") -> {
                            val fileName = entry.name.substringAfterLast('/').safeBackupFileName()
                            val bookId = Regex("""book-(\d+)-cover""").find(fileName)?.groupValues?.get(1)?.toLongOrNull()
                            if (bookId != null) {
                                val file = File(directory, fileName)
                                runCatching {
                                    file.outputStream().use { output ->
                                        zip.copyToLimited(output, MAX_COVER_ENTRY_BYTES)
                                    }
                                    restoredCoverPaths[bookId] = file.absolutePath
                                }.onFailure { file.delete() }
                            }
                        }
                    }
                    zip.closeEntry()
                    entry = zip.getNextEntry()
                }
            }
        }
        applyCompleteBackupManifest(manifest ?: return, restoredCoverPaths)
    }

    private suspend fun importCompleteBackupLegacyJson(uri: Uri) {
        val text = context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.readUtf8TextLimited(MAX_IMPORT_TEXT_CHARS)
        } ?: return
        val manifest = JSONObject(text)
        val restoredCoverPaths = restoreCoverImages(manifest.optJSONArray(KEY_COVER_IMAGES))
        applyCompleteBackupManifest(manifest, restoredCoverPaths)
    }

    private suspend fun applyCompleteBackupManifest(manifest: JSONObject, restoredCoverPaths: Map<Long, String>) {
        val locations = manifest.optJSONArray(KEY_LOCATIONS).toLocationList()
        val books = manifest.optJSONArray(KEY_BOOKS).toBookList().map { book ->
            val restoredCoverPath = restoredCoverPaths[book.id]
            if (restoredCoverPath == null) book else book.copy(coverImagePath = restoredCoverPath)
        }
        val loans = manifest.optJSONArray(KEY_LOANS).toLoanList()
        val borrowers = manifest.optJSONArray(KEY_BORROWERS).toBorrowerList()
        database.withTransaction {
            database.loanDao().clear()
            database.bookDao().clear()
            database.locationDao().clear()
            database.borrowerDao().clear()
            database.locationDao().insertAll(locations)
            database.bookDao().insertAll(books)
            database.borrowerDao().insertAll(borrowers)
            database.loanDao().insertAll(loans)
        }
    }

    private fun InputStream.looksLikeZip(): Boolean {
        val header = ByteArray(4)
        val read = runCatching { read(header) }.getOrDefault(-1)
        return read == 4 &&
            header[0] == 0x50.toByte() && header[1] == 0x4B.toByte() &&
            header[2] == 0x03.toByte() && header[3] == 0x04.toByte()
    }

    suspend fun importCsv(uri: Uri): Int = withContext(Dispatchers.IO) {
        val text = context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.readUtf8TextLimited(MAX_IMPORT_TEXT_CHARS)
        } ?: error("Unable to open CSV file")
        val rows = BookCsvCodec.decode(text)
        val existingIsbns = database.bookDao().getAll()
            .flatMap { listOfNotNull(it.isbn10, it.isbn13) }
            .map { it.normalizedIsbn() }
            .toMutableSet()
        var importedCount = 0
        database.withTransaction {
            rows.forEach { row ->
                val candidateIsbns = listOfNotNull(row.book.isbn10, row.book.isbn13)
                if (candidateIsbns.any { it in existingIsbns }) return@forEach

                val locationId = if (
                    !row.room.isNullOrBlank() &&
                    !row.bookcase.isNullOrBlank() &&
                    !row.shelf.isNullOrBlank()
                ) {
                    database.locationDao().getOrCreate(row.room, row.bookcase, row.shelf)
                } else {
                    null
                }
                database.bookDao().insert(row.book.copy(locationId = locationId))
                existingIsbns += candidateIsbns
                importedCount++
            }
        }
        importedCount
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
        .put("purchaseDateEpochMillis", purchaseDateEpochMillis)
        .put("cost", cost)
        .put("seriesName", seriesName)
        .put("mainGenre", mainGenre)
        .put("subGenres", JSONArray(subGenres))
        .put("bookType", bookType)
        .put("edition", edition)
        .put("signedCopy", signedCopy)

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
        .put("borrowerId", borrowerId)

    private fun BorrowerEntity.toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("name", name)
        .put("phone", phone)
        .put("relation", relation)
        .put("createdAtEpochMillis", createdAtEpochMillis)

    private fun BookEntity.openCoverInputStream(): InputStream? {
        val imagePath = coverImagePath?.takeIf(String::isNotBlank) ?: return null
        return runCatching {
            if (imagePath.startsWith("content://")) {
                context.contentResolver.openInputStream(Uri.parse(imagePath))
            } else {
                File(imagePath).takeIf { it.exists() && it.isFile }?.inputStream()
            }
        }.getOrNull()
    }

    private fun restoreCoverImages(array: JSONArray?): Map<Long, String> {
        if (array == null) return emptyMap()
        val directory = File(context.filesDir, COVER_DIRECTORY).also { it.mkdirs() }
        return buildMap {
            for (index in 0 until array.length()) {
                val json = array.optJSONObject(index) ?: continue
                val bookId = json.optLong("bookId").takeIf { it > 0L } ?: continue
                val data = json.optString("dataBase64").takeIf(String::isNotBlank) ?: continue
                val bytes = runCatching { Base64.decode(data, Base64.DEFAULT) }.getOrNull() ?: continue
                val fileName = json.optString("fileName").takeIf(String::isNotBlank)?.safeBackupFileName()
                    ?: "book-$bookId-cover.jpg"
                val file = File(directory, fileName)
                runCatching {
                    file.outputStream().use { it.write(bytes) }
                    put(bookId, file.absolutePath)
                }
            }
        }
    }

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
                purchaseDateEpochMillis = json.optNullableLong("purchaseDateEpochMillis"),
                cost = json.optNullableDouble("cost"),
                seriesName = json.optNullableString("seriesName"),
                mainGenre = json.optNullableString("mainGenre"),
                subGenres = json.optStringArray("subGenres"),
                bookType = json.optNullableString("bookType"),
                edition = json.optNullableString("edition"),
                signedCopy = json.optBoolean("signedCopy"),
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
                borrowerId = json.optNullableLong("borrowerId"),
            )
        }
    }

    private fun JSONArray?.toBorrowerList(): List<BorrowerEntity> {
        if (this == null) return emptyList()
        return List(length()) { index -> optJSONObject(index) }.filterNotNull().map { json ->
            BorrowerEntity(
                id = json.optLong("id"),
                name = json.optString("name"),
                phone = json.optNullableString("phone"),
                relation = json.optNullableString("relation"),
                createdAtEpochMillis = json.optLong("createdAtEpochMillis", System.currentTimeMillis()),
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

    private fun writeCsv(uri: Uri, csv: String) {
        val stream = context.contentResolver.openOutputStream(uri) ?: error("Unable to create CSV file")
        stream.use { output ->
            output.write(UTF8_BOM)
            output.bufferedWriter(Charsets.UTF_8).apply {
                write(csv)
                flush()
            }
        }
    }

    private fun InputStream.readUtf8TextLimited(maxChars: Int): String {
        val reader = bufferedReader(Charsets.UTF_8)
        val result = StringBuilder(minOf(maxChars, DEFAULT_BUFFER_SIZE))
        val buffer = CharArray(DEFAULT_BUFFER_SIZE)
        while (true) {
            val count = reader.read(buffer)
            if (count < 0) break
            if (result.length + count > maxChars) throw IOException("Backup text entry exceeds the allowed size")
            result.append(buffer, 0, count)
        }
        return result.toString()
    }

    private fun InputStream.copyToLimited(output: OutputStream, maxBytes: Long) {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var totalBytes = 0L
        while (true) {
            val count = read(buffer)
            if (count < 0) break
            totalBytes += count
            if (totalBytes > maxBytes) throw IOException("Backup cover entry exceeds the allowed size")
            output.write(buffer, 0, count)
        }
    }

    private fun String.safeBackupFileName(): String =
        replace(Regex("""[^A-Za-z0-9._-]"""), "_").take(80).ifBlank { "cover.jpg" }

    private companion object {
        const val SCHEMA_VERSION = 1
        const val JSON_INDENT_SPACES = 2
        const val COVER_DIRECTORY = "covers"
        const val KEY_SCHEMA_VERSION = "schemaVersion"
        const val KEY_BACKUP_KIND = "backupKind"
        const val BACKUP_KIND_COMPLETE = "complete"
        const val KEY_EXPORTED_AT = "exportedAtEpochMillis"
        const val KEY_BOOKS = "books"
        const val KEY_LOCATIONS = "locations"
        const val KEY_LOANS = "loans"
        const val KEY_BORROWERS = "borrowers"
        const val KEY_COVER_IMAGES = "coverImages"
        const val MANIFEST_ENTRY_NAME = "backup.json"
        const val COVER_ENTRY_DIR = "covers"
        const val MAX_MANIFEST_CHARS = 16 * 1024 * 1024
        const val MAX_IMPORT_TEXT_CHARS = 64 * 1024 * 1024
        const val MAX_COVER_ENTRY_BYTES = 25L * 1024 * 1024
        val UTF8_BOM = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())
    }
}
