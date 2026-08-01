package com.mj.homelibrary.ui

import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mj.homelibrary.BuildConfig
import com.mj.homelibrary.R

private data class HomeLibraryHelpCard(
    @StringRes val titleRes: Int,
    @StringRes val bodyRes: Int,
    val icon: ImageVector,
)

private data class HomeLibraryHelpSection(
    val group: HelpGroup,
    @StringRes val titleRes: Int,
    @StringRes val previewRes: Int,
    @StringRes val contentRes: Int,
    val icon: ImageVector,
)

private enum class HelpGroup(@StringRes val titleRes: Int, @StringRes val descRes: Int) {
    Everyday(R.string.help_group_library_title, R.string.help_group_library_desc),
    Organization(R.string.help_group_organization_title, R.string.help_group_organization_desc),
    Data(R.string.help_group_data_title, R.string.help_group_data_desc),
}

@Composable
fun HelpAboutScreen(onBack: () -> Unit) {
    var expandedSection by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val onboardingCards = remember {
        listOf(
            HomeLibraryHelpCard(R.string.help_onboard_scan_title, R.string.help_onboard_scan_body, Icons.Outlined.QrCodeScanner),
            HomeLibraryHelpCard(R.string.help_onboard_shelves_title, R.string.help_onboard_shelves_body, Icons.Outlined.Place),
            HomeLibraryHelpCard(R.string.help_onboard_loans_title, R.string.help_onboard_loans_body, Icons.Outlined.Handshake),
            HomeLibraryHelpCard(R.string.help_onboard_backup_title, R.string.help_onboard_backup_body, Icons.Outlined.CloudDone),
        )
    }
    val sections = remember {
        listOf(
            HomeLibraryHelpSection(HelpGroup.Everyday, R.string.help_section_add_books_title, R.string.help_section_add_books_preview, R.string.help_section_add_books_content, Icons.Outlined.MenuBook),
            HomeLibraryHelpSection(HelpGroup.Everyday, R.string.help_section_isbn_title, R.string.help_section_isbn_preview, R.string.help_section_isbn_content, Icons.Outlined.Search),
            HomeLibraryHelpSection(HelpGroup.Everyday, R.string.help_section_covers_ratings_title, R.string.help_section_covers_ratings_preview, R.string.help_section_covers_ratings_content, Icons.Outlined.Star),
            HomeLibraryHelpSection(HelpGroup.Organization, R.string.help_section_shelves_title, R.string.help_section_shelves_preview, R.string.help_section_shelves_content, Icons.Outlined.Place),
            HomeLibraryHelpSection(HelpGroup.Organization, R.string.help_section_filters_title, R.string.help_section_filters_preview, R.string.help_section_filters_content, Icons.Outlined.AutoStories),
            HomeLibraryHelpSection(HelpGroup.Organization, R.string.help_section_loans_title, R.string.help_section_loans_preview, R.string.help_section_loans_content, Icons.Outlined.Handshake),
            HomeLibraryHelpSection(HelpGroup.Data, R.string.help_section_backup_title, R.string.help_section_backup_preview, R.string.help_section_backup_content, Icons.Outlined.CloudDone),
            HomeLibraryHelpSection(HelpGroup.Data, R.string.help_section_privacy_title, R.string.help_section_privacy_preview, R.string.help_section_privacy_content, Icons.Outlined.Security),
            HomeLibraryHelpSection(HelpGroup.Data, R.string.help_section_appearance_title, R.string.help_section_appearance_preview, R.string.help_section_appearance_content, Icons.Outlined.Palette),
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.space_screen)),
        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_2xl)),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item { HelpHeader(onBack = onBack) }
        item {
            Text(
                text = stringResource(R.string.help_welcome_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(R.string.help_welcome_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )
        }
        item {
            HelpOnboardingCards(cards = onboardingCards)
        }
        HelpGroup.entries.forEach { group ->
            item(key = "group_${group.name}") {
                HelpGroupHeader(group = group)
            }
            itemsIndexed(
                sections.filter { it.group == group },
                key = { index, section -> "${group.name}_${section.titleRes}_$index" },
            ) { _, section ->
                val sectionId = section.titleRes
                CollapsibleHelpCard(
                    section = section,
                    expanded = expandedSection == sectionId,
                    onToggle = {
                        expandedSection = if (expandedSection == sectionId) null else sectionId
                    },
                )
            }
        }
        item {
            AboutFooter(
                onShare = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.help_share_home_library_text))
                    }
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.help_share_home_library)))
                },
            )
        }
    }
}

@Composable
fun LocalDataPrivacyScreen(onBack: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.space_screen)),
        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_2xl)),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { HelpHeader(titleRes = R.string.settings_privacy_data_title, onBack = onBack) }
        item {
            PrivacyInfoCard(
                icon = Icons.Outlined.Security,
                titleRes = R.string.privacy_local_title,
                bodyRes = R.string.privacy_local_body,
            )
        }
        item {
            PrivacyInfoCard(
                icon = Icons.Outlined.Search,
                titleRes = R.string.privacy_lookup_title,
                bodyRes = R.string.privacy_lookup_body,
            )
        }
        item {
            PrivacyInfoCard(
                icon = Icons.Outlined.CloudDone,
                titleRes = R.string.privacy_backup_title,
                bodyRes = R.string.privacy_backup_body,
            )
        }
    }
}

@Composable
private fun HelpHeader(
    @StringRes titleRes: Int = R.string.help_screen_title,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.space_lg)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = stringResource(R.string.action_back))
        }
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HelpOnboardingCards(cards: List<HomeLibraryHelpCard>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = stringResource(R.string.help_start_here),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            maxItemsInEachRow = 2,
        ) {
            cards.forEach { card ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(156.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(34.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(card.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
                            }
                        }
                        Text(stringResource(card.titleRes), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = stringResource(card.bodyRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HelpGroupHeader(group: HelpGroup) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(stringResource(group.titleRes), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        Text(stringResource(group.descRes), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CollapsibleHelpCard(
    section: HomeLibraryHelpSection,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp).animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(42.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(section.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
                Text(
                    text = stringResource(section.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f).padding(start = 14.dp),
                )
                Icon(
                    imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = stringResource(if (expanded) R.string.help_cd_collapse else R.string.help_cd_expand),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = stringResource(if (expanded) section.contentRes else section.previewRes),
                style = if (expanded) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                lineHeight = if (expanded) 22.sp else 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AboutFooter(onShare: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(80.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(40.dp))
            }
        }
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text(stringResource(R.string.help_tagline), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = stringResource(R.string.help_version_prefix, BuildConfig.VERSION_NAME),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                .padding(horizontal = 12.dp, vertical = 4.dp),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HelpFooterActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Share,
                labelRes = R.string.help_footer_share,
                onClick = onShare,
            )
            HelpFooterActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Info,
                labelRes = R.string.help_footer_package,
                detail = BuildConfig.APPLICATION_ID,
                onClick = {},
            )
        }
    }
}

@Composable
private fun HelpFooterActionButton(
    modifier: Modifier,
    icon: ImageVector,
    @StringRes labelRes: Int,
    detail: String? = null,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(icon, contentDescription = stringResource(labelRes), modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(stringResource(labelRes), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        if (detail != null) {
            Text(detail, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun PrivacyInfoCard(
    icon: ImageVector,
    @StringRes titleRes: Int,
    @StringRes bodyRes: Int,
) {
    ElevatedCard(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(stringResource(titleRes), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(stringResource(bodyRes), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp)
            }
        }
    }
}
