package com.mj.homelibrary.ui

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import com.mj.homelibrary.ui.theme.ExpressiveMotion
import com.mj.homelibrary.ui.theme.expressiveClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mj.homelibrary.R
import com.mj.homelibrary.data.AppFontFamily
import com.mj.homelibrary.data.AppearanceSettings
import com.mj.homelibrary.data.BackgroundTintLevel
import com.mj.homelibrary.data.ColorSource
import com.mj.homelibrary.data.FabPlacement
import com.mj.homelibrary.data.FontScalePreference
import com.mj.homelibrary.data.ThemeColorIntensity
import com.mj.homelibrary.data.ThemePreference
import kotlin.math.roundToInt

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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.space_screen)),
        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_2xl)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_lg)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            AppearanceHeader(onBack = onBack)
        }
        item {
            AppearanceSectionLabel(R.string.settings_theme)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ThemePreference.entries.forEach { theme ->
                    ThemeModeCard(
                        modifier = Modifier.weight(1f),
                        themePreference = theme,
                        selected = settings.themePreference == theme,
                        onClick = { onThemeSelected(theme) },
                    )
                }
            }
        }
        item {
            AppearanceSectionLabel(R.string.settings_colors_label)
            ColorSourceSelector(
                selected = settings.colorSource,
                onSelected = onColorSourceSelected,
            )
        }
        item {
            LabeledDiscreteSlider(
                titleRes = R.string.settings_color_intensity_title,
                selectedLabel = settings.themeColorIntensity.label(),
                labels = ThemeColorIntensity.entries.map { it.label() },
                selectedIndex = ThemeColorIntensity.entries.indexOf(settings.themeColorIntensity),
                onSelectedIndex = { onThemeColorIntensitySelected(ThemeColorIntensity.entries[it]) },
            )
        }
        item {
            LabeledDiscreteSlider(
                titleRes = R.string.settings_background_tint_title,
                selectedLabel = settings.backgroundTintLevel.label(),
                labels = BackgroundTintLevel.entries.map { it.label() },
                selectedIndex = BackgroundTintLevel.entries.indexOf(settings.backgroundTintLevel),
                onSelectedIndex = { onBackgroundTintLevelSelected(BackgroundTintLevel.entries[it]) },
            )
        }
        item {
            AppearanceSectionLabel(R.string.settings_font)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AppFontFamily.entries.forEach { fontFamily ->
                    FontFamilyCard(
                        modifier = Modifier.weight(1f),
                        fontFamily = fontFamily,
                        selected = settings.appFontFamily == fontFamily,
                        onClick = { onFontFamilySelected(fontFamily) },
                    )
                }
            }
        }
        item {
            AppearanceSectionLabel(R.string.settings_ui_font_size_label)
            LabeledDiscreteSlider(
                titleRes = R.string.settings_ui_font_size_label,
                selectedLabel = settings.fontScalePreference.label(),
                labels = FontScalePreference.entries.map { it.label() },
                selectedIndex = FontScalePreference.entries.indexOf(settings.fontScalePreference),
                onSelectedIndex = { onFontScaleSelected(FontScalePreference.entries[it]) },
                showTitle = false,
            )
        }
        item {
            FollowFontScaleRow(
                checked = settings.followUiFontScale,
                onCheckedChange = onFollowUiFontScaleChanged,
            )
            AnimatedVisibility(
                visible = !settings.followUiFontScale,
                enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(tween(150, easing = ExpressiveMotion.EmphasizedDecelerate)),
                exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut(tween(150, easing = ExpressiveMotion.EmphasizedAccelerate)),
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    AppearanceSectionLabel(R.string.settings_content_font_size_label)
                    LabeledDiscreteSlider(
                        titleRes = R.string.settings_content_font_size_label,
                        selectedLabel = settings.contentFontScalePreference.label(),
                        labels = FontScalePreference.entries.map { it.label() },
                        selectedIndex = FontScalePreference.entries.indexOf(settings.contentFontScalePreference),
                        onSelectedIndex = { onContentFontScaleSelected(FontScalePreference.entries[it]) },
                        showTitle = false,
                    )
                }
            }
        }
        item {
            AppearanceSectionLabel(R.string.settings_layout_label)
            Text(
                text = stringResource(R.string.settings_layout_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(start = 6.dp, bottom = 10.dp),
            )
            FabPlacementSelector(
                selected = settings.fabPlacement,
                onSelected = onFabPlacementSelected,
            )
        }
    }
}

@Composable
private fun AppearanceHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.action_back),
            )
        }
        Text(
            text = stringResource(R.string.settings_appearance),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun AppearanceSectionLabel(@StringRes labelRes: Int) {
    Text(
        text = stringResource(labelRes),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth().padding(start = 2.dp, top = 6.dp, bottom = 4.dp),
    )
}

@Composable
private fun ThemeModeCard(
    modifier: Modifier,
    themePreference: ThemePreference,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val icon = when (themePreference) {
        ThemePreference.SYSTEM -> Icons.Outlined.SettingsBrightness
        ThemePreference.LIGHT -> Icons.Outlined.LightMode
        ThemePreference.DARK -> Icons.Outlined.DarkMode
        ThemePreference.AMOLED -> Icons.Outlined.Palette
    }
    val cardColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    ElevatedCard(
        modifier = modifier
            .height(124.dp)
            .expressiveClickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = cardColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.TopCenter)
                    .clip(CircleShape)
                    .background(if (selected) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (selected) Icons.Outlined.Check else icon,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = themePreference.label(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorSourceSelector(
    selected: ColorSource,
    onSelected: (ColorSource) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        ColorSource.entries.forEachIndexed { index, source ->
            SegmentedButton(
                selected = source == selected,
                onClick = { onSelected(source) },
                shape = SegmentedButtonDefaults.itemShape(index, ColorSource.entries.size),
                label = {
                    Text(
                        text = source.label(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        }
    }
}

@Composable
private fun LabeledDiscreteSlider(
    @StringRes titleRes: Int,
    selectedLabel: String,
    labels: List<String>,
    selectedIndex: Int,
    onSelectedIndex: (Int) -> Unit,
    showTitle: Boolean = true,
) {
    AppearancePanel {
        if (showTitle) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = selectedLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Slider(
            value = selectedIndex.toFloat(),
            onValueChange = { value ->
                onSelectedIndex(value.roundToInt().coerceIn(labels.indices))
            },
            valueRange = 0f..labels.lastIndex.toFloat(),
            steps = (labels.size - 2).coerceAtLeast(0),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                activeTickColor = MaterialTheme.colorScheme.onPrimary,
                inactiveTickColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
            ),
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun FontFamilyCard(
    modifier: Modifier,
    fontFamily: AppFontFamily,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val cardColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier.expressiveClickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(108.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(cardColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.settings_font_sample),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = fontFamily.previewFamily(),
                    fontWeight = FontWeight.SemiBold,
                ),
                color = contentColor,
            )
        }
        Text(
            text = fontFamily.label(),
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun FollowFontScaleRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.settings_follow_ui_font_scale_label),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FabPlacementSelector(
    selected: FabPlacement,
    onSelected: (FabPlacement) -> Unit,
) {
    val options = listOf(FabPlacement.LEFT, FabPlacement.RIGHT)
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, placement ->
            val icon = if (placement == FabPlacement.LEFT) {
                Icons.AutoMirrored.Outlined.KeyboardArrowLeft
            } else {
                Icons.AutoMirrored.Outlined.KeyboardArrowRight
            }
            SegmentedButton(
                selected = placement == selected,
                onClick = { onSelected(placement) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                label = {
                    Text(
                        text = placement.label(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        }
    }
}

@Composable
private fun AppearancePanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content,
        )
    }
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
