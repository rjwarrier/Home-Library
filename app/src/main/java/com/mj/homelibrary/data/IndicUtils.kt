package com.mj.homelibrary.data

object IndicUtils {

    /**
     * Detects the predominant Indic language from character Unicode blocks.
     */
    fun detectIndicLanguage(text: String): LanguageCode? {
        if (text.isBlank()) return null

        var malayalamCount = 0
        var tamilCount = 0
        var devanagariCount = 0
        var teluguCount = 0
        var kannadaCount = 0
        var bengaliCount = 0
        var gujaratiCount = 0
        var gurmukhiCount = 0
        var odiaCount = 0
        var urduCount = 0

        for (ch in text) {
            when (ch.code) {
                in 0x0D00..0x0D7F -> malayalamCount++
                in 0x0B80..0x0BFF -> tamilCount++
                in 0x0900..0x097F -> devanagariCount++
                in 0x0C00..0x0C7F -> teluguCount++
                in 0x0C80..0x0CFF -> kannadaCount++
                in 0x0980..0x09FF -> bengaliCount++
                in 0x0A80..0x0AFF -> gujaratiCount++
                in 0x0A00..0x0A7F -> gurmukhiCount++
                in 0x0B00..0x0B7F -> odiaCount++
                in 0x0600..0x06FF -> urduCount++
            }
        }

        val counts = listOf(
            LanguageCode.Malayalam to malayalamCount,
            LanguageCode.Tamil to tamilCount,
            LanguageCode.Hindi to devanagariCount,
            LanguageCode.Telugu to teluguCount,
            LanguageCode.Kannada to kannadaCount,
            LanguageCode.Bengali to bengaliCount,
            LanguageCode.Gujarati to gujaratiCount,
            LanguageCode.Punjabi to gurmukhiCount,
            LanguageCode.Odia to odiaCount,
            LanguageCode.Urdu to urduCount,
        )

        val maxEntry = counts.maxByOrNull { it.second } ?: return null
        return if (maxEntry.second >= 2) maxEntry.first else null
    }

    /**
     * Checks if a character belongs to any Indic or Brahmic Unicode block.
     */
    fun isIndicChar(ch: Char): Boolean {
        return when (ch.code) {
            in 0x0900..0x0D7F, in 0x0600..0x06FF -> true
            else -> false
        }
    }

    /**
     * Normalizes text for search matching, stripping punctuation and diacritics.
     */
    fun normalizeForSearch(text: String): String {
        return text.trim()
            .lowercase()
            .replace(Regex("[\\p{Punct}\\s]+"), " ")
            .trim()
    }

    /**
     * Popular Indian publishing houses for instant autocomplete.
     */
    val popularIndicPublishers = listOf(
        "DC Books",
        "Mathrubhumi Books",
        "Current Books",
        "NBS (Sahithya Pravarthaka Co-operative)",
        "Green Books",
        "Olive Publications",
        "Chintha Publishers",
        "Sahitya Akademi",
        "National Book Trust (NBT)",
        "Gita Press",
        "Rajkamal Prakashan",
        "Vani Prakashan",
        "Lokbharti Prakashan",
        "Bharatiya Jnanpith",
        "Kalachuvadu Publications",
        "Vikatan Publications",
        "Kizhakku Pathippagam",
        "Navakarnataka Publications",
        "Sapna Book House",
        "Ananda Publishers",
        "Dey's Publishing",
        "Jaico Publishing House",
        "Rupa Publications",
        "Motilal Banarsidass",
        "Penguin India",
        "HarperCollins India",
        "Westland Books",
    )

    /**
     * Curated Indic literary categories and genres.
     */
    val popularIndicGenres = listOf(
        "Kavitha (Poetry)",
        "Ithihasam & Purana",
        "Natakam (Play)",
        "Sahithyam (Literature)",
        "Cherukatha (Short Stories)",
        "Novels & Fiction",
        "Bhakti & Darshanam",
        "Dalit Sahitya",
        "Athmakatha (Autobiography)",
        "Yathravivarana (Travelogue)",
        "Vyakthichithram (Biography)",
        "Vimarshanam (Literary Criticism)",
        "Indian History & Culture",
        "Veda & Upanishads",
    )
}
