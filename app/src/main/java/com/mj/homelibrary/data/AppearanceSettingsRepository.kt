package com.mj.homelibrary.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK,
    AMOLED,
}

enum class ColorSource {
    MATERIAL_YOU,
    CUSTOM,
}

enum class ThemeColorIntensity(val level: Float) {
    MUTED(0.72f),
    NORMAL(1.0f),
    VIVID(1.18f),
    POP(1.36f),
}

enum class BackgroundTintLevel(val amount: Float) {
    CLEAN(0.02f),
    SOFT(0.06f),
    RICH(0.11f),
    DEEP(0.17f),
}

enum class AppFontFamily {
    SANS_SERIF,
    SERIF,
    MONO,
}

enum class FontScalePreference(val scale: Float) {
    SMALLER(0.90f),
    SMALL(0.96f),
    NORMAL(1.0f),
    LARGE(1.08f),
    LARGER(1.16f),
}

enum class FabPlacement {
    LEFT,
    RIGHT,
}

data class AppearanceSettings(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val colorSource: ColorSource = ColorSource.MATERIAL_YOU,
    val themeColorIntensity: ThemeColorIntensity = ThemeColorIntensity.NORMAL,
    val backgroundTintLevel: BackgroundTintLevel = BackgroundTintLevel.SOFT,
    val appFontFamily: AppFontFamily = AppFontFamily.SANS_SERIF,
    val fontScalePreference: FontScalePreference = FontScalePreference.NORMAL,
    val contentFontScalePreference: FontScalePreference = FontScalePreference.NORMAL,
    val followUiFontScale: Boolean = true,
    val fabPlacement: FabPlacement = FabPlacement.RIGHT,
)

class AppearanceSettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _settings = MutableStateFlow(readSettings())

    val settings: StateFlow<AppearanceSettings> = _settings.asStateFlow()

    fun setThemePreference(preference: ThemePreference) = update(KEY_THEME, preference)

    fun setColorSource(source: ColorSource) = update(KEY_COLOR_SOURCE, source)

    fun setThemeColorIntensity(intensity: ThemeColorIntensity) = update(KEY_COLOR_INTENSITY, intensity)

    fun setBackgroundTintLevel(level: BackgroundTintLevel) = update(KEY_BACKGROUND_TINT, level)

    fun setAppFontFamily(fontFamily: AppFontFamily) = update(KEY_APP_FONT_FAMILY, fontFamily)

    fun setFontScalePreference(preference: FontScalePreference) = update(KEY_FONT_SCALE, preference)

    fun setContentFontScalePreference(preference: FontScalePreference) = update(KEY_CONTENT_FONT_SCALE, preference)

    fun setFollowUiFontScale(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_FOLLOW_UI_FONT_SCALE, enabled).apply()
        _settings.value = readSettings()
    }

    fun setFabPlacement(placement: FabPlacement) = update(KEY_FAB_PLACEMENT, placement)

    private fun <T : Enum<T>> update(key: String, value: T) {
        prefs.edit().putString(key, value.name).apply()
        _settings.value = readSettings()
    }

    private fun readSettings(): AppearanceSettings =
        AppearanceSettings(
            themePreference = readEnum(KEY_THEME, ThemePreference.SYSTEM),
            colorSource = readEnum(KEY_COLOR_SOURCE, ColorSource.MATERIAL_YOU),
            themeColorIntensity = readEnum(KEY_COLOR_INTENSITY, ThemeColorIntensity.NORMAL),
            backgroundTintLevel = readEnum(KEY_BACKGROUND_TINT, BackgroundTintLevel.SOFT),
            appFontFamily = readEnum(KEY_APP_FONT_FAMILY, AppFontFamily.SANS_SERIF),
            fontScalePreference = readEnum(KEY_FONT_SCALE, FontScalePreference.NORMAL),
            contentFontScalePreference = readEnum(KEY_CONTENT_FONT_SCALE, FontScalePreference.NORMAL),
            followUiFontScale = prefs.getBoolean(KEY_FOLLOW_UI_FONT_SCALE, true),
            fabPlacement = readEnum(KEY_FAB_PLACEMENT, FabPlacement.RIGHT),
        )

    private inline fun <reified T : Enum<T>> readEnum(key: String, fallback: T): T =
        prefs.getString(key, null)?.let { saved ->
            enumValues<T>().firstOrNull { it.name == saved }
        } ?: fallback

    private companion object {
        const val PREFS_NAME = "appearance_settings"
        const val KEY_THEME = "theme"
        const val KEY_COLOR_SOURCE = "color_source"
        const val KEY_COLOR_INTENSITY = "color_intensity"
        const val KEY_BACKGROUND_TINT = "background_tint"
        const val KEY_APP_FONT_FAMILY = "app_font_family"
        const val KEY_FONT_SCALE = "font_scale"
        const val KEY_CONTENT_FONT_SCALE = "content_font_scale"
        const val KEY_FOLLOW_UI_FONT_SCALE = "follow_ui_font_scale"
        const val KEY_FAB_PLACEMENT = "fab_placement"
    }
}
