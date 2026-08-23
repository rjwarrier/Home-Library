package com.mj.homelibrary.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IsbnTest {

    @Test
    fun indianIsbnDetection() {
        assertTrue("978-81-264-1234-1".isIndianIsbn())
        assertTrue("9788126412341".isIndianIsbn())
        assertTrue("978-93-5000-000-1".isIndianIsbn())
        assertTrue("8126412340".isIndianIsbn())
        assertTrue("9350000001".isIndianIsbn())

        // Non-Indian ISBNs
        assertFalse("978-0-13-468599-1".isIndianIsbn())
        assertFalse("9780306406157".isIndianIsbn())
    }

    @Test
    fun isbn13ToIsbn10Conversion() {
        val isbn13 = "9780306406157"
        val expectedIsbn10 = "0306406152"
        assertEquals(expectedIsbn10, isbn13.toEquivalentIsbn10())

        val indianIsbn13 = "9788126412341"
        val converted = indianIsbn13.toEquivalentIsbn10()
        assertNotNull(converted)
        assertEquals(10, converted?.length)
        assertTrue(converted?.isValidIsbn() == true)
    }

    @Test
    fun isbn10ToIsbn13Conversion() {
        val isbn10 = "0306406152"
        val expectedIsbn13 = "9780306406157"
        assertEquals(expectedIsbn13, isbn10.toEquivalentIsbn13())
    }
}
