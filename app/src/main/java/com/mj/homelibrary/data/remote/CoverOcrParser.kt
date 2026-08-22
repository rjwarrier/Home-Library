package com.mj.homelibrary.data.remote

import android.graphics.Rect
import com.google.mlkit.vision.text.Text
import com.mj.homelibrary.data.validIsbnOrNull

data class OcrBookDetails(
    val title: String = "",
    val authors: List<String> = emptyList(),
    val publisher: String? = null,
    val year: Int? = null,
    val rawIsbn: String? = null,
)

data class ParsedOcrLine(
    val text: String,
    val boundingBox: Rect?,
    val fontHeight: Float,
    val top: Int,
    val bottom: Int,
)

object CoverOcrParser {

    private val yearRegex = Regex("""\b(18\d{2}|19\d{2}|20[0-2]\d)\b""")
    private val isbnPrefixRegex = Regex("""(?i)ISBN[-:\s]*([0-9Xx-]{10,17})""")
    private val authorPrefixRegex = Regex("""(?i)^(?:by|written\s+by|authored\s+by|author[:\s]+)\s*(.+)""")
    private val publisherKeywords = listOf(
        "publisher", "publishers", "publishing", "press", "books", "publications",
        "editions", "house", "media", "dc books", "mathrubhumi", "kottayam", "sahitya",
        "harper", "penguin", "vintage", "scholastic", "oxford", "cambridge", "bloomsbury",
        "simon", "schuster", "macmillan", "hachette", "routledge", "wiley"
    )

    fun parse(mlKitText: Text): OcrBookDetails {
        val lines = mutableListOf<ParsedOcrLine>()

        for (block in mlKitText.textBlocks) {
            for (line in block.lines) {
                val cleanText = line.text.trim()
                if (cleanText.length < 2) continue

                val box = line.boundingBox
                val fontHeight = box?.let { (it.bottom - it.top).toFloat() } ?: 20f
                val top = box?.top ?: 0
                val bottom = box?.bottom ?: 0

                lines.add(
                    ParsedOcrLine(
                        text = cleanText,
                        boundingBox = box,
                        fontHeight = fontHeight,
                        top = top,
                        bottom = bottom,
                    )
                )
            }
        }

        if (lines.isEmpty()) return OcrBookDetails()

        var detectedIsbn: String? = null
        var detectedYear: Int? = null
        var detectedPublisher: String? = null
        val detectedAuthors = mutableListOf<String>()

        // 1. Check for ISBN
        for (line in lines) {
            val isbnMatch = isbnPrefixRegex.find(line.text)
            if (isbnMatch != null) {
                val candidate = isbnMatch.groupValues[1].validIsbnOrNull()
                if (candidate != null) {
                    detectedIsbn = candidate
                    break
                }
            }
        }

        // 2. Check for Year
        for (line in lines) {
            val yearMatch = yearRegex.find(line.text)
            if (yearMatch != null) {
                val parsedYear = yearMatch.groupValues[1].toIntOrNull()
                if (parsedYear != null && parsedYear in 1800..2030) {
                    detectedYear = parsedYear
                    break
                }
            }
        }

        // 3. Filter candidates for Title and Author
        val nonMetaLines = lines.filter { line ->
            val text = line.text
            val isIsbnLine = isbnPrefixRegex.containsMatchIn(text) || text.validIsbnOrNull() != null
            val isYearOnly = text.matches(Regex("""^\d{4}$"""))
            val isBarcodeJunk = text.startsWith("|||") || text.matches(Regex("""^[\d\s-]{12,}$"""))
            val isPrice = text.matches(Regex("""(?i)^[₹$€£]?\s*\d+(?:\.\d{2})?\s*(?:rs|inr|usd)?$"""))
            !isIsbnLine && !isYearOnly && !isBarcodeJunk && !isPrice
        }

        if (nonMetaLines.isEmpty()) {
            return OcrBookDetails(rawIsbn = detectedIsbn, year = detectedYear)
        }

        // 4. Find Publisher line if any keyword matches
        for (line in nonMetaLines) {
            val lower = line.text.lowercase()
            if (publisherKeywords.any { lower.contains(it) } && detectedPublisher == null) {
                val cleanedPublisher = line.text
                    .replace(Regex("""(?i)^(published\s+by|publisher[:\s]+)"""), "")
                    .trim()
                if (cleanedPublisher.isNotBlank()) {
                    detectedPublisher = cleanedPublisher
                }
            }
        }

        // 5. Detect Author prefixed with "By"
        for (line in nonMetaLines) {
            val match = authorPrefixRegex.find(line.text)
            if (match != null) {
                val authorName = match.groupValues[1].trim()
                if (authorName.isNotBlank() && authorName !in detectedAuthors) {
                    detectedAuthors.add(authorName)
                }
            }
        }

        // 6. Identify Title & remaining Author by spatial & font hierarchy
        val contentCandidates = nonMetaLines.filter { line ->
            line.text != detectedPublisher && !detectedAuthors.contains(line.text) && !authorPrefixRegex.containsMatchIn(line.text)
        }

        val sortedByProminence = contentCandidates.sortedWith(
            compareByDescending<ParsedOcrLine> { it.fontHeight }
                .thenBy { it.top }
        )

        val title: String
        if (sortedByProminence.isNotEmpty()) {
            val mostProminent = sortedByProminence.first()
            
            // If there's an adjacent line with similar font height right below/above, group it
            val adjacentTitleLines = sortedByProminence
                .filter { Math.abs(it.top - mostProminent.bottom) < 50 || Math.abs(it.bottom - mostProminent.top) < 50 || it == mostProminent }
                .filter { it.fontHeight >= mostProminent.fontHeight * 0.65f }
                .sortedBy { it.top }

            title = adjacentTitleLines.joinToString(" ") { it.text }

            if (detectedAuthors.isEmpty() && sortedByProminence.size > adjacentTitleLines.size) {
                val nextCandidate = sortedByProminence.firstOrNull { it !in adjacentTitleLines }
                if (nextCandidate != null && nextCandidate.text.length in 3..50) {
                    detectedAuthors.add(nextCandidate.text)
                }
            }
        } else {
            title = nonMetaLines.firstOrNull()?.text.orEmpty()
        }

        return OcrBookDetails(
            title = title,
            authors = detectedAuthors,
            publisher = detectedPublisher,
            year = detectedYear,
            rawIsbn = detectedIsbn,
        )
    }
}
