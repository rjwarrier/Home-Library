package com.mj.homelibrary.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

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
    displaySmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
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
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)

private val HomeLibraryShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun HomeLibraryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme: ColorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme -> dynamicDarkColorScheme(context)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicLightColorScheme(context)
        darkTheme -> WarmDarkColors
        else -> WarmLightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HomeLibraryTypography,
        shapes = HomeLibraryShapes,
        content = content,
    )
}
