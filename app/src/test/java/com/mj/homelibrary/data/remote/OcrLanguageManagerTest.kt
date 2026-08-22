package com.mj.homelibrary.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OcrLanguageManagerTest {

    @Test
    fun availableLanguagesIncludeMalayalamAndMajorScripts() {
        val languages = OcrLanguageManager.availableLanguages
        assertTrue(languages.isNotEmpty())

        val mal = languages.firstOrNull { it.code == "mal" }
        assertNotNull(mal)
        assertEquals("Malayalam", mal?.englishName)
        assertEquals("മലയാളം", mal?.nativeName)
        assertEquals("ml", mal?.bookLanguageCode)

        val tam = languages.firstOrNull { it.code == "tam" }
        assertNotNull(tam)
        assertEquals("Tamil", tam?.englishName)
        assertEquals("தமிழ்", tam?.nativeName)

        val hin = languages.firstOrNull { it.code == "hin" }
        assertNotNull(hin)
        assertEquals("Hindi", hin?.englishName)
        assertEquals("हिन्दी", hin?.nativeName)
    }
}
