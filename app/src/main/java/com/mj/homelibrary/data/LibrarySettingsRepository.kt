package com.mj.homelibrary.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

data class LibrarySettings(
    val defaultSort: BookSortCode = BookSortCode.Recent,
    val defaultGridMode: Boolean = true,
    val loanRemindersEnabled: Boolean = true,
    val loanReminderLeadDays: Int = 0,
    val backupReminderDays: Int = 30,
    val lastCompleteBackupEpochMillis: Long? = null,
    val readingGoal: Int = 24,
    val mainGenres: List<String> = DEFAULT_MAIN_GENRES,
    val subGenresByMainGenre: Map<String, List<String>> = DEFAULT_SUB_GENRES_BY_MAIN_GENRE,
    val primaryLanguage: String = LanguageCode.English.code,
) {
    val subGenres: List<String>
        get() = mainGenres
            .flatMap { subGenresFor(it) }
            .distinctBy { it.lowercase() }

    fun subGenresFor(mainGenre: String): List<String> =
        subGenresByMainGenre.entries
            .firstOrNull { it.key.equals(mainGenre, ignoreCase = true) }
            ?.value
            .orEmpty()

    companion object {
        val DEFAULT_MAIN_GENRES = listOf("Fiction", "Non-Fiction")
        val DEFAULT_SUB_GENRES_BY_MAIN_GENRE = linkedMapOf(
            "Fiction" to listOf(
                "Action", "Adventure", "Alternate History", "Anthology", "Apocalyptic",
                "Bildungsroman", "Children's", "Classics", "Cli-Fi", "Coming-of-Age",
                "Contemporary", "Cozy Mystery", "Crime", "Cyberpunk", "Dark Academia",
                "Detective", "Drama", "Dystopian", "Epic Fantasy", "Fairy Tale",
                "Family Saga", "Fantasy", "Folklore", "GameLit", "Ghost Story",
                "Gothic", "Graphic Novel", "Hard Sci-Fi", "Historical Fiction", "Horror",
                "Humor", "Legal Thriller", "Literary Fiction", "LitRPG", "Magical Realism",
                "Military Fiction", "Mystery", "Mythology", "Noir", "Paranormal",
                "Political Thriller", "Post-Apocalyptic", "Psychological Thriller", "Retelling",
                "Romance", "Romantic Comedy", "Satire", "Short Stories", "Social Fiction",
                "Soft Sci-Fi", "Space Opera", "Speculative Fiction", "Spy", "Steampunk",
                "Superhero", "Supernatural", "Suspense", "Thriller", "Urban Fantasy",
                "War Fiction", "Western", "Young Adult", "Poetry",
            ),
            "Non-Fiction" to listOf(
                "Accounting", "AI and Machine Learning", "Anthropology", "Architecture", "Art",
                "Archaeology", "Astronomy", "Autobiography", "Biography", "Business",
                "Career", "Chemistry", "Climate", "Computer Science", "Cooking",
                "Crafts", "Cultural Studies", "Current Affairs", "Data Science", "Design",
                "Economics", "Education", "Engineering", "Environment", "Essays",
                "Family", "Film", "Finance", "Food Writing", "Gardening", "Geography",
                "Health", "History", "Home Improvement", "Investing", "Journalism",
                "Language", "Law", "Leadership", "Management", "Marketing",
                "Mathematics", "Medicine", "Memoir", "Military History", "Mindfulness",
                "Music", "Nature", "Parenting", "Personal Development", "Philosophy",
                "Photography", "Physics", "Politics", "Productivity", "Psychology",
                "Reference", "Religion", "Science", "Self-Help", "Social Science",
                "Sociology", "Spirituality", "Sports", "Technology", "Travel",
                "True Crime", "Wellness", "Writing", "Yoga",
            ),
        )
        val DEFAULT_SUB_GENRES = DEFAULT_SUB_GENRES_BY_MAIN_GENRE.values.flatten()
    }
}

class LibrarySettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _settings = MutableStateFlow(readSettings())

    val settings: StateFlow<LibrarySettings> = _settings.asStateFlow()

    fun setDefaultSort(sort: BookSortCode) {
        prefs.edit().putString(KEY_DEFAULT_SORT, sort.name).apply()
        refresh()
    }

    fun setDefaultGridMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DEFAULT_GRID_MODE, enabled).apply()
        refresh()
    }

    fun setLoanRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LOAN_REMINDERS_ENABLED, enabled).apply()
        refresh()
    }

    fun setLoanReminderLeadDays(days: Int) {
        prefs.edit().putInt(KEY_LOAN_REMINDER_LEAD_DAYS, days.coerceIn(0, 14)).apply()
        refresh()
    }

    fun setBackupReminderDays(days: Int) {
        prefs.edit().putInt(KEY_BACKUP_REMINDER_DAYS, days.coerceIn(0, 90)).apply()
        refresh()
    }

    fun markCompleteBackupExported(epochMillis: Long = System.currentTimeMillis()) {
        prefs.edit().putLong(KEY_LAST_COMPLETE_BACKUP, epochMillis).apply()
        refresh()
    }

    fun setReadingGoal(goal: Int) {
        prefs.edit().putInt(KEY_READING_GOAL, goal.coerceIn(1, 365)).apply()
        refresh()
    }

    fun setPrimaryLanguage(languageCode: String) {
        prefs.edit().putString(KEY_PRIMARY_LANGUAGE, languageCode).apply()
        refresh()
    }

    fun addMainGenre(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return
        val settings = readSettings()
        val current = settings.mainGenres
        if (current.any { it.equals(trimmed, ignoreCase = true) }) return
        writeStringList(KEY_MAIN_GENRES, current + trimmed)
        writeSubGenreMap(settings.subGenresByMainGenre + (trimmed to emptyList()))
        refresh()
    }

    fun removeMainGenre(name: String) {
        val settings = readSettings()
        writeStringList(KEY_MAIN_GENRES, settings.mainGenres.filterNot { it.equals(name, ignoreCase = true) })
        writeSubGenreMap(settings.subGenresByMainGenre.filterKeys { !it.equals(name, ignoreCase = true) })
        refresh()
    }

    fun addSubGenre(mainGenre: String, name: String) {
        val genre = mainGenre.trim()
        val trimmed = name.trim()
        if (genre.isBlank() || trimmed.isBlank()) return
        val settings = readSettings()
        val owner = settings.mainGenres.firstOrNull { it.equals(genre, ignoreCase = true) } ?: return
        val current = settings.subGenresFor(owner)
        if (current.any { it.equals(trimmed, ignoreCase = true) }) return
        writeSubGenreMap(settings.subGenresByMainGenre.withGenre(owner, current + trimmed))
        refresh()
    }

    fun removeSubGenre(mainGenre: String, name: String) {
        val settings = readSettings()
        val owner = settings.mainGenres.firstOrNull { it.equals(mainGenre, ignoreCase = true) } ?: return
        writeSubGenreMap(settings.subGenresByMainGenre.withGenre(owner, settings.subGenresFor(owner).filterNot { it.equals(name, ignoreCase = true) }))
        refresh()
    }

    private fun refresh() {
        _settings.value = readSettings()
    }

    private fun readSettings(): LibrarySettings {
        val mainGenres = readStringList(KEY_MAIN_GENRES, LibrarySettings.DEFAULT_MAIN_GENRES)
        val subGenresByMainGenre = readSeededSubGenreMap(mainGenres)
        return LibrarySettings(
            defaultSort = readEnum(KEY_DEFAULT_SORT, BookSortCode.Recent),
            defaultGridMode = prefs.getBoolean(KEY_DEFAULT_GRID_MODE, true),
            loanRemindersEnabled = prefs.getBoolean(KEY_LOAN_REMINDERS_ENABLED, true),
            loanReminderLeadDays = prefs.getInt(KEY_LOAN_REMINDER_LEAD_DAYS, 0).coerceIn(0, 14),
            backupReminderDays = prefs.getInt(KEY_BACKUP_REMINDER_DAYS, 30).coerceIn(0, 90),
            lastCompleteBackupEpochMillis = prefs.takeIf { it.contains(KEY_LAST_COMPLETE_BACKUP) }
                ?.getLong(KEY_LAST_COMPLETE_BACKUP, 0L)
                ?.takeIf { it > 0L },
            readingGoal = prefs.getInt(KEY_READING_GOAL, 24).coerceIn(1, 365),
            mainGenres = mainGenres,
            subGenresByMainGenre = subGenresByMainGenre,
            primaryLanguage = prefs.getString(KEY_PRIMARY_LANGUAGE, null) ?: LanguageCode.English.code,
        )
    }

    private fun readStringList(key: String, fallback: List<String>): List<String> {
        val raw = prefs.getString(key, null) ?: return fallback
        return runCatching {
            val array = JSONArray(raw)
            List(array.length()) { array.optString(it) }.filter(String::isNotBlank)
        }.getOrDefault(fallback)
    }

    private fun writeStringList(key: String, values: List<String>) {
        prefs.edit().putString(key, JSONArray(values).toString()).apply()
    }

    private fun readSeededSubGenreMap(mainGenres: List<String>): Map<String, List<String>> {
        var map = readSubGenreMap(mainGenres)
        if (prefs.getInt(KEY_SUB_GENRE_DEFAULTS_VERSION, 0) < SUB_GENRE_DEFAULTS_VERSION) {
            map = map.mergeDefaultSubGenres(mainGenres)
            writeSubGenreMap(map)
        }
        return map
    }

    private fun readSubGenreMap(mainGenres: List<String>): Map<String, List<String>> {
        val raw = prefs.getString(KEY_SUB_GENRES_BY_MAIN_GENRE, null)
        if (raw != null) {
            val parsed = runCatching {
                val json = JSONObject(raw)
                mainGenres.associateWith { mainGenre ->
                    val storedKey = json.keys().asSequence().firstOrNull { it.equals(mainGenre, ignoreCase = true) } ?: mainGenre
                    val array = json.optJSONArray(storedKey) ?: JSONArray()
                    List(array.length()) { array.optString(it) }.cleanGenreValues()
                }
            }.getOrNull()
            if (parsed != null) return parsed
        }
        return migrateLegacySubGenres(mainGenres)
    }

    private fun migrateLegacySubGenres(mainGenres: List<String>): Map<String, List<String>> {
        val legacy = readStringList(KEY_SUB_GENRES, LibrarySettings.DEFAULT_SUB_GENRES).cleanGenreValues()
        val defaultOwners = LibrarySettings.DEFAULT_SUB_GENRES_BY_MAIN_GENRE
        val grouped = mainGenres.associateWith { mainGenre ->
            defaultOwners.entries
                .firstOrNull { it.key.equals(mainGenre, ignoreCase = true) }
                ?.value
                ?.filter { default -> legacy.any { it.equals(default, ignoreCase = true) } }
                .orEmpty()
        }.toMutableMap()
        val knownDefaults = defaultOwners.values.flatten()
        val customLegacy = legacy.filterNot { candidate ->
            knownDefaults.any { it.equals(candidate, ignoreCase = true) } ||
                grouped.values.flatten().any { it.equals(candidate, ignoreCase = true) }
        }
        val fallbackMainGenre = mainGenres.firstOrNull()
        if (fallbackMainGenre != null && customLegacy.isNotEmpty()) {
            grouped[fallbackMainGenre] = (grouped[fallbackMainGenre].orEmpty() + customLegacy).cleanGenreValues()
        }
        return grouped
    }

    private fun writeSubGenreMap(values: Map<String, List<String>>) {
        val json = JSONObject()
        values.forEach { (mainGenre, subGenres) ->
            json.put(mainGenre, JSONArray(subGenres.cleanGenreValues()))
        }
        prefs.edit()
            .putString(KEY_SUB_GENRES_BY_MAIN_GENRE, json.toString())
            .putString(KEY_SUB_GENRES, JSONArray(values.values.flatten().cleanGenreValues()).toString())
            .putInt(KEY_SUB_GENRE_DEFAULTS_VERSION, SUB_GENRE_DEFAULTS_VERSION)
            .apply()
    }

    private fun Map<String, List<String>>.mergeDefaultSubGenres(mainGenres: List<String>): Map<String, List<String>> =
        mainGenres.associateWith { mainGenre ->
            val current = entries.firstOrNull { it.key.equals(mainGenre, ignoreCase = true) }?.value.orEmpty()
            val defaults = LibrarySettings.DEFAULT_SUB_GENRES_BY_MAIN_GENRE.entries
                .firstOrNull { it.key.equals(mainGenre, ignoreCase = true) }
                ?.value
                .orEmpty()
            (current + defaults).cleanGenreValues()
        }

    private fun Map<String, List<String>>.withGenre(mainGenre: String, subGenres: List<String>): Map<String, List<String>> =
        entries.associate { (key, value) ->
            key to if (key.equals(mainGenre, ignoreCase = true)) subGenres.cleanGenreValues() else value
        }

    private fun List<String>.cleanGenreValues(): List<String> =
        map(String::trim)
            .filter(String::isNotBlank)
            .distinctBy { it.lowercase() }

    private inline fun <reified T : Enum<T>> readEnum(key: String, fallback: T): T =
        prefs.getString(key, null)?.let { saved ->
            enumValues<T>().firstOrNull { it.name == saved }
        } ?: fallback

    private companion object {
        const val PREFS_NAME = "library_settings"
        const val KEY_DEFAULT_SORT = "default_sort"
        const val KEY_DEFAULT_GRID_MODE = "default_grid_mode"
        const val KEY_LOAN_REMINDERS_ENABLED = "loan_reminders_enabled"
        const val KEY_LOAN_REMINDER_LEAD_DAYS = "loan_reminder_lead_days"
        const val KEY_BACKUP_REMINDER_DAYS = "backup_reminder_days"
        const val KEY_LAST_COMPLETE_BACKUP = "last_complete_backup"
        const val KEY_READING_GOAL = "reading_goal"
        const val KEY_MAIN_GENRES = "main_genres"
        const val KEY_SUB_GENRES = "sub_genres"
        const val KEY_SUB_GENRES_BY_MAIN_GENRE = "sub_genres_by_main_genre"
        const val KEY_SUB_GENRE_DEFAULTS_VERSION = "sub_genre_defaults_version"
        const val KEY_PRIMARY_LANGUAGE = "primary_language"
        const val SUB_GENRE_DEFAULTS_VERSION = 1
    }
}
