package com.mj.homelibrary.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val WarmLightColors = lightColorScheme(
    primary = Color(0xFF6F4E27),
    onPrimary = Color.White,
    secondary = Color(0xFF755C3B),
    tertiary = Color(0xFF8C6D1F),
    background = Color(0xFFFFFBF2),
    surface = Color(0xFFFFF8EA),
    surfaceVariant = Color(0xFFE9DDC7),
    onSurface = Color(0xFF201A16),
    outline = Color(0xFF7B6C5D),
)

private val WarmDarkColors = darkColorScheme(
    primary = Color(0xFFE4BE7A),
    onPrimary = Color(0xFF3E2800),
    secondary = Color(0xFFD6C4A8),
    tertiary = Color(0xFFE4C66B),
    background = Color(0xFF18120E),
    surface = Color(0xFF211914),
    surfaceVariant = Color(0xFF51453A),
    onSurface = Color(0xFFF1E7DA),
    outline = Color(0xFFA39687),
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
        typography = MaterialTheme.typography,
        content = content,
    )
}
