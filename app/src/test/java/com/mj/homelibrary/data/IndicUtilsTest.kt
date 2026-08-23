package com.mj.homelibrary.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IndicUtilsTest {

    @Test
    fun detectMalayalamScript() {
        val detected = IndicUtils.detectIndicLanguage("രണ്ടാമൂഴം - എം. ടി. വാസുദേവൻ നായർ")
        assertEquals(LanguageCode.Malayalam, detected)
    }

    @Test
    fun detectTamilScript() {
        val detected = IndicUtils.detectIndicLanguage("பொன்னியின் செல்வன் - கல்கி")
        assertEquals(LanguageCode.Tamil, detected)
    }

    @Test
    fun detectDevanagariHindiScript() {
        val detected = IndicUtils.detectIndicLanguage("गोदान - मुंशी प्रेमचंद")
        assertEquals(LanguageCode.Hindi, detected)
    }

    @Test
    fun detectTeluguScript() {
        val detected = IndicUtils.detectIndicLanguage("కన్యాశుల్కం - గురజాడ అప్పారావు")
        assertEquals(LanguageCode.Telugu, detected)
    }

    @Test
    fun detectKannadaScript() {
        val detected = IndicUtils.detectIndicLanguage("ಪರ್ವ - ಎಸ್. ಎಲ್. ಭೈರಪ್ಪ")
        assertEquals(LanguageCode.Kannada, detected)
    }

    @Test
    fun detectBengaliScript() {
        val detected = IndicUtils.detectIndicLanguage("গীতাঞ্জলি - রবীন্দ্রনাথ ঠাকুর")
        assertEquals(LanguageCode.Bengali, detected)
    }

    @Test
    fun detectGujaratiScript() {
        val detected = IndicUtils.detectIndicLanguage("સરસ્વતીચંદ્ર - ગોવર્ધનરામ ત્રિપાઠી")
        assertEquals(LanguageCode.Gujarati, detected)
    }

    @Test
    fun detectGurmukhiPunjabiScript() {
        val detected = IndicUtils.detectIndicLanguage("ਪਿੰਜਰ - ਅੰਮ੍ਰਿਤਾ ਪ੍ਰੀਤਮ")
        assertEquals(LanguageCode.Punjabi, detected)
    }

    @Test
    fun detectOdiaScript() {
        val detected = IndicUtils.detectIndicLanguage("ଛମାଣ ଆଠଗୁଣ୍ଠ - ଫକୀର ମୋହନ ସେନାପତି")
        assertEquals(LanguageCode.Odia, detected)
    }

    @Test
    fun detectUrduScript() {
        val detected = IndicUtils.detectIndicLanguage("دیوان غالب - مرزا اسد اللہ خان غالب")
        assertEquals(LanguageCode.Urdu, detected)
    }

    @Test
    fun indicPublishersAndGenresAvailable() {
        assertTrue(IndicUtils.popularIndicPublishers.contains("DC Books"))
        assertTrue(IndicUtils.popularIndicPublishers.contains("Mathrubhumi Books"))
        assertTrue(IndicUtils.popularIndicPublishers.contains("Sahitya Akademi"))
        assertTrue(IndicUtils.popularIndicPublishers.contains("Gita Press"))

        assertTrue(IndicUtils.popularIndicGenres.contains("Kavitha (Poetry)"))
        assertTrue(IndicUtils.popularIndicGenres.contains("Ithihasam & Purana"))
    }
}
