package com.mj.homelibrary.data

import com.mj.homelibrary.data.entity.BookEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class BookCsvCodecTest {
    @Test
    fun fullMetadataRoundTripsWithoutCoverFields() {
        val acquisitionDate = LocalDate.of(2024, 2, 3).toEpochMillis()
        val purchaseDate = LocalDate.of(2024, 2, 4).toEpochMillis()
        val addedDate = LocalDate.of(2024, 2, 5).toEpochMillis()
        val source = BookCsvRow(
            book = BookEntity(
                title = "A Title, With Comma",
                subtitle = "Second \"Edition\"",
                authors = listOf("Author One", "എഴുത്തുകാരൻ"),
                languageCode = "ml",
                originalScriptTitle = "മലയാളം",
                tags = listOf("history", "reference"),
                isbn10 = "0306406152",
                isbn13 = "9780306406157",
                publisher = "Publisher",
                publishedYear = 2024,
                pageCount = 420,
                coverImagePath = "/private/cover.jpg",
                coverUrl = "https://example.com/cover.jpg",
                formatCode = "hardcover",
                acquisitionDateEpochMillis = acquisitionDate,
                notes = "First line\nSecond line, with comma",
                rating = 4.5f,
                readStatusCode = "finished",
                positionNote = "Left side",
                addedDateEpochMillis = addedDate,
                purchaseDateEpochMillis = purchaseDate,
                cost = 799.5,
                seriesName = "Series One",
                mainGenre = "Non-fiction",
                subGenres = listOf("History", "India"),
                bookType = "Reference",
                edition = "2nd",
                signedCopy = true,
            ),
            room = "Study",
            bookcase = "Bookcase 2",
            shelf = "Shelf 4",
        )

        val csv = BookCsvCodec.encode(listOf(source))
        val decoded = BookCsvCodec.decode(csv).single()

        assertEquals(source.book.copy(coverImagePath = null, coverUrl = null), decoded.book)
        assertEquals(source.room, decoded.room)
        assertEquals(source.bookcase, decoded.bookcase)
        assertEquals(source.shelf, decoded.shelf)
        assertFalse(csv.contains("coverImagePath", ignoreCase = true))
        assertFalse(csv.contains("coverUrl", ignoreCase = true))
        assertFalse(csv.contains("/private/cover.jpg"))
    }

    @Test
    fun legacyTenColumnCsvStillImports() {
        val csv = "\uFEFFtitle,authors,language,isbn13,isbn10,publisher,published_year,tags,read_status,rating\r\n" +
            "Legacy Book,One Author; Two Author,en,9780306406157,0306406152,Old Press,1988,science; classic,finished,4.0\r\n"

        val row = BookCsvCodec.decode(csv).single()

        assertEquals("Legacy Book", row.book.title)
        assertEquals(listOf("One Author", "Two Author"), row.book.authors)
        assertEquals(listOf("science", "classic"), row.book.tags)
        assertEquals("9780306406157", row.book.isbn13)
        assertEquals("finished", row.book.readStatusCode)
        assertNull(row.room)
    }

    @Test
    fun templateContainsOnlyTheHeaderAndNoImageColumns() {
        val template = BookCsvCodec.template()
        val lines = template.trimEnd().lines()

        assertEquals(1, lines.size)
        assertTrue(lines.single().startsWith("title,authors,language,isbn13,isbn10"))
        assertFalse(template.contains("cover", ignoreCase = true))
    }

    private fun LocalDate.toEpochMillis(): Long =
        atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
