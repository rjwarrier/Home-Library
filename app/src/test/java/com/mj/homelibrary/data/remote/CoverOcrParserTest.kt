package com.mj.homelibrary.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CoverOcrParserTest {

    @Test
    fun parsedOcrLineCalculatesProminence() {
        val line = ParsedOcrLine(
            text = "Foundation and Empire",
            boundingBox = null,
            fontHeight = 36f,
            top = 100,
            bottom = 136,
        )
        assertEquals("Foundation and Empire", line.text)
        assertEquals(36f, line.fontHeight, 0.01f)
    }

    @Test
    fun ocrBookDetailsHoldsParsedFields() {
        val details = OcrBookDetails(
            title = "Foundation and Empire",
            authors = listOf("Isaac Asimov"),
            publisher = "Gnome Press",
            year = 1952,
            rawIsbn = "9780553293371",
        )
        assertEquals("Foundation and Empire", details.title)
        assertEquals(listOf("Isaac Asimov"), details.authors)
        assertEquals("Gnome Press", details.publisher)
        assertEquals(1952, details.year)
        assertEquals("9780553293371", details.rawIsbn)
    }
}
