package com.mj.homelibrary.ui.theme

import android.graphics.Color as AndroidColor
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.mj.homelibrary.data.AppFontFamily
import com.mj.homelibrary.data.AppearanceSettings
import com.mj.homelibrary.data.BackgroundTintLevel
import com.mj.homelibrary.data.ColorSource
import com.mj.homelibrary.data.ThemeColorIntensity

private val WarmLightColors = lightColorScheme(
    primary = Color(0xFF6F4E27),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDDB6),
    onPrimaryContainer = Color(0xFF261700),
    secondary = Color(0xFF755C3B),
    secondaryContainer = Color(0xFFFFDEB0),
    onSecondaryContainer = Color(0xFF2A1800),
    tertiary = Color(0xFF8C6D1F),
    tertiaryContainer = Color(0xFFFFE08C),
    onTertiaryContainer = Color(0xFF2B2000),
    background = Color(0xFFFFFBF2),
    surface = Color(0xFFFFF8EA),
    surfaceContainerHigh = Color(0xFFFFF1DC),
    surfaceVariant = Color(0xFFE9DDC7),
    onSurface = Color(0xFF201A16),
    onSurfaceVariant = Color(0xFF574539),
    outline = Color(0xFF7B6C5D),
    outlineVariant = Color(0xFFD5C3AE),
    error = Color(0xFF8F4B37),
    errorContainer = Color(0xFFFFDAD2),
    onErrorContainer = Color(0xFF3B0906),
)

private val WarmDarkColors = darkColorScheme(
    primary = Color(0xFFE4BE7A),
    onPrimary = Color(0xFF3E2800),
    primaryContainer = Color(0xFF58411F),
    onPrimaryContainer = Color(0xFFFFDDB6),
    secondary = Color(0xFFD6C4A8),
    secondaryContainer = Color(0xFF5B4326),
    onSecondaryContainer = Color(0xFFFFDEB0),
    tertiary = Color(0xFFE4C66B),
    tertiaryContainer = Color(0xFF4E3D14),
    onTertiaryContainer = Color(0xFFFFE08C),
    background = Color(0xFF18120E),
    surface = Color(0xFF211914),
    surfaceContainerHigh = Color(0xFF2C231C),
    surfaceVariant = Color(0xFF51453A),
    onSurface = Color(0xFFF1E7DA),
    onSurfaceVariant = Color(0xFFD5C3AE),
    outline = Color(0xFFA39687),
    outlineVariant = Color(0xFF4A3D33),
    error = Color(0xFFFFB4A4),
    errorContainer = Color(0xFF5C2317),
    onErrorContainer = Color(0xFFFFDAD2),
)

private val HomeLibraryTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)

// Mirrors the corner_* scale in dimens.xml. Kept as literal .dp here (not
// dimensionResource) because this is a module-level val, outside any @Composable.
private val HomeLibraryShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp), // corner_sm
    small = RoundedCornerShape(12.dp), // corner_control
    medium = RoundedCornerShape(16.dp), // corner_card
    large = RoundedCornerShape(20.dp), // corner_prominent
    extraLarge = RoundedCornerShape(32.dp), // corner_sheet
)

@Composable
fun HomeLibraryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appearanceSettings: AppearanceSettings = AppearanceSettings(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val effectiveDarkTheme = when (appearanceSettings.themePreference) {
        com.mj.homelibrary.data.ThemePreference.SYSTEM -> darkTheme
        com.mj.homelibrary.data.ThemePreference.LIGHT -> false
        com.mj.homelibrary.data.ThemePreference.DARK -> true
        com.mj.homelibrary.data.ThemePreference.AMOLED -> true
    }
    val amoledTheme = appearanceSettings.themePreference == com.mj.homelibrary.data.ThemePreference.AMOLED
    val baseScheme: ColorScheme = when {
        appearanceSettings.colorSource == ColorSource.MATERIAL_YOU &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            effectiveDarkTheme -> dynamicDarkColorScheme(context)
        appearanceSettings.colorSource == ColorSource.MATERIAL_YOU &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicLightColorScheme(context)
        effectiveDarkTheme -> WarmDarkColors
        else -> WarmLightColors
    }
    val colorScheme = baseScheme
        .withAmoledIfNeeded(amoledTheme)
        .withAppearanceTuning(
            darkTheme = effectiveDarkTheme,
            amoledTheme = amoledTheme,
            intensity = appearanceSettings.themeColorIntensity,
            backgroundTint = appearanceSettings.backgroundTintLevel,
        )
    val fontFamily = appearanceSettings.appFontFamily.toFontFamily()
    val typography = remember(appearanceSettings.appFontFamily) {
        HomeLibraryTypography.withFontFamily(fontFamily)
    }
    val currentDensity = LocalDensity.current
    val activeFontScale = if (appearanceSettings.followUiFontScale) {
        appearanceSettings.fontScalePreference.scale
    } else {
        appearanceSettings.contentFontScalePreference.scale
    }
    val scaledDensity = remember(currentDensity, activeFontScale) {
        Density(
            density = currentDensity.density,
            fontScale = currentDensity.fontScale * activeFontScale,
        )
    }

    CompositionLocalProvider(LocalDensity provides scaledDensity) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = HomeLibraryShapes,
            content = content,
        )
    }
}

private fun ColorScheme.withAmoledIfNeeded(amoledTheme: Boolean): ColorScheme =
    if (!amoledTheme) {
        this
    } else {
        copy(
            background = Color.Black,
            surface = Color.Black,
            surfaceVariant = Color(0xFF1C1C1C),
            surfaceContainerLowest = Color.Black,
            surfaceContainerLow = Color(0xFF0E0E0E),
            surfaceContainer = Color(0xFF151515),
            surfaceContainerHigh = Color(0xFF1C1C1C),
            surfaceContainerHighest = Color(0xFF242424),
        )
    }

private fun ColorScheme.withAppearanceTuning(
    darkTheme: Boolean,
    amoledTheme: Boolean,
    intensity: ThemeColorIntensity,
    backgroundTint: BackgroundTintLevel,
): ColorScheme =
    copy(
        primary = primary.scaleColorIntensity(intensity),
        primaryContainer = primaryContainer.scaleColorIntensity(intensity),
        secondary = secondary.scaleColorIntensity(intensity),
        secondaryContainer = secondaryContainer.scaleColorIntensity(intensity),
        tertiary = tertiary.scaleColorIntensity(intensity),
        tertiaryContainer = tertiaryContainer.scaleColorIntensity(intensity),
        inversePrimary = inversePrimary.scaleColorIntensity(intensity),
        background = background.tintSurface(primary, backgroundTint, darkTheme, amoledTheme, 0.72f),
        surface = surface.tintSurface(primary, backgroundTint, darkTheme, amoledTheme, 0.84f),
        surfaceVariant = surfaceVariant.tintSurface(primary, backgroundTint, darkTheme, amoledTheme, 1.05f),
        surfaceContainerLowest = surfaceContainerLowest.tintSurface(primary, backgroundTint, darkTheme, amoledTheme, 0.66f),
        surfaceContainerLow = surfaceContainerLow.tintSurface(primary, backgroundTint, darkTheme, amoledTheme, 0.86f),
        surfaceContainer = surfaceContainer.tintSurface(primary, backgroundTint, darkTheme, amoledTheme, 0.96f),
        surfaceContainerHigh = surfaceContainerHigh.tintSurface(primary, backgroundTint, darkTheme, amoledTheme, 1.08f),
        surfaceContainerHighest = surfaceContainerHighest.tintSurface(primary, backgroundTint, darkTheme, amoledTheme, 1.18f),
        outline = lerp(outline, primary.scaleColorIntensity(intensity), if (darkTheme || amoledTheme) 0.18f else 0.12f),
    )

private fun Color.tintSurface(
    primary: Color,
    backgroundTint: BackgroundTintLevel,
    darkTheme: Boolean,
    amoledTheme: Boolean,
    strength: Float,
): Color {
    if (amoledTheme && this == Color.Black) return Color.Black
    val maxAlpha = if (darkTheme || amoledTheme) 0.22f else 0.14f
    val alpha = (backgroundTint.amount * strength).coerceIn(0f, maxAlpha)
    return primary.copy(alpha = alpha).compositeOver(this)
}

private fun Color.scaleColorIntensity(intensity: ThemeColorIntensity): Color {
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(toArgb(), hsv)
    hsv[1] = (hsv[1] * intensity.level).coerceIn(0f, 1f)
    hsv[2] = when (intensity) {
        ThemeColorIntensity.MUTED -> (hsv[2] * 0.94f).coerceIn(0f, 1f)
        ThemeColorIntensity.NORMAL -> hsv[2]
        ThemeColorIntensity.VIVID -> (hsv[2] * 1.03f).coerceIn(0f, 1f)
        ThemeColorIntensity.POP -> (hsv[2] * 1.07f).coerceIn(0f, 1f)
    }
    return Color(AndroidColor.HSVToColor(hsv))
}

private fun AppFontFamily.toFontFamily(): FontFamily =
    when (this) {
        AppFontFamily.SANS_SERIF -> FontFamily.SansSerif
        AppFontFamily.SERIF -> FontFamily.Serif
        AppFontFamily.MONO -> FontFamily.Monospace
    }

private fun Typography.withFontFamily(fontFamily: FontFamily): Typography =
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily),
    )
