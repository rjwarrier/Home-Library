package com.mj.homelibrary.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mj.homelibrary.R
import com.mj.homelibrary.data.AppFontFamily
import com.mj.homelibrary.data.AppearanceSettings
import com.mj.homelibrary.data.BackgroundTintLevel
import com.mj.homelibrary.data.ColorSource
import com.mj.homelibrary.data.FabPlacement
import com.mj.homelibrary.data.FontScalePreference
import com.mj.homelibrary.data.ThemeColorIntensity
import com.mj.homelibrary.data.ThemePreference

@Composable
fun AppearanceSettingsScreen(
    settings: AppearanceSettings,
    onBack: () -> Unit,
    onThemeSelected: (ThemePreference) -> Unit,
    onColorSourceSelected: (ColorSource) -> Unit,
    onThemeColorIntensitySelected: (ThemeColorIntensity) -> Unit,
    onBackgroundTintLevelSelected: (BackgroundTintLevel) -> Unit,
    onFontFamilySelected: (AppFontFamily) -> Unit,
    onFontScaleSelected: (FontScalePreference) -> Unit,
    onContentFontScaleSelected: (FontScalePreference) -> Unit,
    onFollowUiFontScaleChanged: (Boolean) -> Unit,
    onFabPlacementSelected: (FabPlacement) -> Unit,
) {
    SettingsPage(titleRes = R.string.settings_appearance, onBack = onBack) {
        item {
            SettingsGroup(label = stringResource(R.string.settings_theme)) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ThemePreference.entries.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            rowItems.forEach { theme ->
                                ChoiceTile(
                                    modifier = Modifier.weight(1f),
                                    icon = theme.icon(),
                                    label = theme.label(),
                                    selected = settings.themePreference == theme,
                                    onClick = { onThemeSelected(theme) },
                                )
                            }
                            if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        item {
            SettingsGroup(label = stringResource(R.string.settings_colors_label)) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    ColorSource.entries.forEachIndexed { index, source ->
                        SegmentedButton(
                            selected = source == settings.colorSource,
                            onClick = { onColorSourceSelected(source) },
                            shape = SegmentedButtonDefaults.itemShape(index, ColorSource.entries.size),
                        ) { Text(source.label(), maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    }
                }
                SettingsDivider()
                SettingsSliderRow(
                    title = stringResource(R.string.settings_color_intensity_title),
                    valueLabel = settings.themeColorIntensity.label(),
                    value = ThemeColorIntensity.entries.indexOf(settings.themeColorIntensity).toFloat(),
                    valueRange = 0f..ThemeColorIntensity.entries.lastIndex.toFloat(),
                    steps = (ThemeColorIntensity.entries.size - 2).coerceAtLeast(0),
                    onValueChange = { onThemeColorIntensitySelected(ThemeColorIntensity.entries[it.toInt().coerceIn(ThemeColorIntensity.entries.indices)]) },
                )
                SettingsDivider()
                SettingsSliderRow(
                    title = stringResource(R.string.settings_background_tint_title),
                    valueLabel = settings.backgroundTintLevel.label(),
                    value = BackgroundTintLevel.entries.indexOf(settings.backgroundTintLevel).toFloat(),
                    valueRange = 0f..BackgroundTintLevel.entries.lastIndex.toFloat(),
                    steps = (BackgroundTintLevel.entries.size - 2).coerceAtLeast(0),
                    onValueChange = { onBackgroundTintLevelSelected(BackgroundTintLevel.entries[it.toInt().coerceIn(BackgroundTintLevel.entries.indices)]) },
                )
            }
        }
        item {
            SettingsGroup(label = stringResource(R.string.settings_font)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AppFontFamily.entries.forEach { fontFamily ->
                        FontFamilyTile(
                            modifier = Modifier.weight(1f),
                            fontFamily = fontFamily,
                            selected = settings.appFontFamily == fontFamily,
                            onClick = { onFontFamilySelected(fontFamily) },
                        )
                    }
                }
                SettingsDivider()
                SettingsSliderRow(
                    title = stringResource(R.string.settings_ui_font_size_label),
                    valueLabel = settings.fontScalePreference.label(),
                    value = FontScalePreference.entries.indexOf(settings.fontScalePreference).toFloat(),
                    valueRange = 0f..FontScalePreference.entries.lastIndex.toFloat(),
                    steps = (FontScalePreference.entries.size - 2).coerceAtLeast(0),
                    onValueChange = { onFontScaleSelected(FontScalePreference.entries[it.toInt().coerceIn(FontScalePreference.entries.indices)]) },
                )
                SettingsDivider()
                SettingsSwitchRow(
                    title = stringResource(R.string.settings_follow_ui_font_scale_label),
                    checked = settings.followUiFontScale,
                    onCheckedChange = onFollowUiFontScaleChanged,
                )
                AnimatedVisibility(
                    visible = !settings.followUiFontScale,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    Column {
                        SettingsDivider()
                        SettingsSliderRow(
                            title = stringResource(R.string.settings_content_font_size_label),
                            valueLabel = settings.contentFontScalePreference.label(),
                            value = FontScalePreference.entries.indexOf(settings.contentFontScalePreference).toFloat(),
                            valueRange = 0f..FontScalePreference.entries.lastIndex.toFloat(),
                            steps = (FontScalePreference.entries.size - 2).coerceAtLeast(0),
                            onValueChange = { onContentFontScaleSelected(FontScalePreference.entries[it.toInt().coerceIn(FontScalePreference.entries.indices)]) },
                        )
                    }
                }
            }
        }
        item {
            SettingsGroup(label = stringResource(R.string.settings_layout_label)) {
                SettingsBody(stringResource(R.string.settings_layout_desc))
                val options = listOf(FabPlacement.LEFT, FabPlacement.RIGHT)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    options.forEachIndexed { index, placement ->
                        SegmentedButton(
                            selected = placement == settings.fabPlacement,
                            onClick = { onFabPlacementSelected(placement) },
                            shape = SegmentedButtonDefaults.itemShape(index, options.size),
                            icon = {
                                SegmentedButtonDefaults.Icon(
                                    active = placement == settings.fabPlacement,
                                    inactiveContent = {
                                        Icon(
                                            if (placement == FabPlacement.LEFT) Icons.AutoMirrored.Outlined.KeyboardArrowLeft else Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                            contentDescription = null,
                                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                                        )
                                    },
                                )
                            },
                        ) { Text(placement.label(), maxLines = 1) }
                    }
                }
            }
        }
    }
}

/** Selectable tile: tonal with primary outline when selected. Grows with font scale instead of clipping. */
@Composable
private fun ChoiceTile(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card)),
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun FontFamilyTile(
    modifier: Modifier,
    fontFamily: AppFontFamily,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_card)),
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(R.string.settings_font_sample),
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = fontFamily.previewFamily(), fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Clip,
            )
            Text(
                text = fontFamily.label(),
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun ThemePreference.icon(): ImageVector = when (this) {
    ThemePreference.SYSTEM -> Icons.Outlined.SettingsBrightness
    ThemePreference.LIGHT -> Icons.Outlined.LightMode
    ThemePreference.DARK -> Icons.Outlined.DarkMode
    ThemePreference.AMOLED -> Icons.Outlined.Palette
}

@Composable
private fun ThemePreference.label(): String =
    stringResource(
        when (this) {
            ThemePreference.SYSTEM -> R.string.settings_theme_system
            ThemePreference.LIGHT -> R.string.settings_theme_light
            ThemePreference.DARK -> R.string.settings_theme_dark
            ThemePreference.AMOLED -> R.string.settings_theme_amoled
        },
    )

@Composable
private fun ColorSource.label(): String =
    stringResource(
        when (this) {
            ColorSource.MATERIAL_YOU -> R.string.settings_color_source_material_you
            ColorSource.CUSTOM -> R.string.settings_color_source_custom
        },
    )

@Composable
private fun ThemeColorIntensity.label(): String =
    stringResource(
        when (this) {
            ThemeColorIntensity.MUTED -> R.string.settings_intensity_muted
            ThemeColorIntensity.NORMAL -> R.string.settings_intensity_normal
            ThemeColorIntensity.VIVID -> R.string.settings_intensity_vivid
            ThemeColorIntensity.POP -> R.string.settings_intensity_pop
        },
    )

@Composable
private fun BackgroundTintLevel.label(): String =
    stringResource(
        when (this) {
            BackgroundTintLevel.CLEAN -> R.string.settings_background_tint_clean
            BackgroundTintLevel.SOFT -> R.string.settings_background_tint_soft
            BackgroundTintLevel.RICH -> R.string.settings_background_tint_rich
            BackgroundTintLevel.DEEP -> R.string.settings_background_tint_deep
        },
    )

@Composable
private fun AppFontFamily.label(): String =
    stringResource(
        when (this) {
            AppFontFamily.SANS_SERIF -> R.string.settings_font_sans
            AppFontFamily.SERIF -> R.string.settings_font_serif
            AppFontFamily.MONO -> R.string.settings_font_mono
        },
    )

@Composable
private fun FontScalePreference.label(): String =
    stringResource(
        when (this) {
            FontScalePreference.SMALLER -> R.string.settings_font_scale_smaller
            FontScalePreference.SMALL -> R.string.settings_font_scale_small
            FontScalePreference.NORMAL -> R.string.settings_font_scale_normal
            FontScalePreference.LARGE -> R.string.settings_font_scale_large
            FontScalePreference.LARGER -> R.string.settings_font_scale_larger
        },
    )

@Composable
private fun FabPlacement.label(): String =
    stringResource(
        when (this) {
            FabPlacement.LEFT -> R.string.settings_fab_side_left_short
            FabPlacement.RIGHT -> R.string.settings_fab_side_right_short
        },
    )

private fun AppFontFamily.previewFamily(): FontFamily =
    when (this) {
        AppFontFamily.SANS_SERIF -> FontFamily.SansSerif
        AppFontFamily.SERIF -> FontFamily.Serif
        AppFontFamily.MONO -> FontFamily.Monospace
    }
