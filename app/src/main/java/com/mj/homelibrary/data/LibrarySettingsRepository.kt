package com.mj.homelibrary.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray

data class LibrarySettings(
    val defaultSort: BookSortCode = BookSortCode.Recent,
    val defaultGridMode: Boolean = true,
    val loanRemindersEnabled: Boolean = true,
    val loanReminderLeadDays: Int = 0,
    val backupReminderDays: Int = 30,
    val lastCompleteBackupEpochMillis: Long? = null,
    val readingGoal: Int = 24,
    val mainGenres: List<String> = DEFAULT_MAIN_GENRES,
    val subGenres: List<String> = DEFAULT_SUB_GENRES,
    val primaryLanguage: String = LanguageCode.English.code,
) {
    companion object {
        val DEFAULT_MAIN_GENRES = listOf("Fiction", "Non-Fiction")
        val DEFAULT_SUB_GENRES = listOf(
            "Crime", "Thriller", "Mystery", "Sci-Fi", "Fantasy", "Romance", "Horror",
            "Classics", "Young Adult", "Children's", "Poetry", "Biography", "Memoir",
            "History", "Science", "Philosophy", "Self-Help", "Business", "Travel", "Cooking",
        )
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
        val current = readStringList(KEY_MAIN_GENRES, LibrarySettings.DEFAULT_MAIN_GENRES)
        if (current.any { it.equals(trimmed, ignoreCase = true) }) return
        writeStringList(KEY_MAIN_GENRES, current + trimmed)
        refresh()
    }

    fun removeMainGenre(name: String) {
        val current = readStringList(KEY_MAIN_GENRES, LibrarySettings.DEFAULT_MAIN_GENRES)
        writeStringList(KEY_MAIN_GENRES, current.filterNot { it.equals(name, ignoreCase = true) })
        refresh()
    }

    fun addSubGenre(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return
        val current = readStringList(KEY_SUB_GENRES, LibrarySettings.DEFAULT_SUB_GENRES)
        if (current.any { it.equals(trimmed, ignoreCase = true) }) return
        writeStringList(KEY_SUB_GENRES, current + trimmed)
        refresh()
    }

    fun removeSubGenre(name: String) {
        val current = readStringList(KEY_SUB_GENRES, LibrarySettings.DEFAULT_SUB_GENRES)
        writeStringList(KEY_SUB_GENRES, current.filterNot { it.equals(name, ignoreCase = true) })
        refresh()
    }

    private fun refresh() {
        _settings.value = readSettings()
    }

    private fun readSettings(): LibrarySettings =
        LibrarySettings(
            defaultSort = readEnum(KEY_DEFAULT_SORT, BookSortCode.Recent),
            defaultGridMode = prefs.getBoolean(KEY_DEFAULT_GRID_MODE, true),
            loanRemindersEnabled = prefs.getBoolean(KEY_LOAN_REMINDERS_ENABLED, true),
            loanReminderLeadDays = prefs.getInt(KEY_LOAN_REMINDER_LEAD_DAYS, 0).coerceIn(0, 14),
            backupReminderDays = prefs.getInt(KEY_BACKUP_REMINDER_DAYS, 30).coerceIn(0, 90),
            lastCompleteBackupEpochMillis = prefs.takeIf { it.contains(KEY_LAST_COMPLETE_BACKUP) }
                ?.getLong(KEY_LAST_COMPLETE_BACKUP, 0L)
                ?.takeIf { it > 0L },
            readingGoal = prefs.getInt(KEY_READING_GOAL, 24).coerceIn(1, 365),
            mainGenres = readStringList(KEY_MAIN_GENRES, LibrarySettings.DEFAULT_MAIN_GENRES),
            subGenres = readStringList(KEY_SUB_GENRES, LibrarySettings.DEFAULT_SUB_GENRES),
            primaryLanguage = prefs.getString(KEY_PRIMARY_LANGUAGE, null) ?: LanguageCode.English.code,
        )

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
        const val KEY_PRIMARY_LANGUAGE = "primary_language"
    }
}
