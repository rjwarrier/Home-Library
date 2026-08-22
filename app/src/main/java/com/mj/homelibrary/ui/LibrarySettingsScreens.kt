package com.mj.homelibrary.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.draw.rotate
import com.mj.homelibrary.ui.theme.ExpressiveMotion
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mj.homelibrary.R
import com.mj.homelibrary.data.BookSortCode
import com.mj.homelibrary.data.LanguageCode
import com.mj.homelibrary.data.LibrarySettings
import kotlin.math.roundToInt

@Composable
fun LibraryPreferencesScreen(
    settings: LibrarySettings,
    onBack: () -> Unit,
    onDefaultGridModeChange: (Boolean) -> Unit,
    onDefaultSortChange: (BookSortCode) -> Unit,
    onReadingGoalChange: (Int) -> Unit,
    onPrimaryLanguageChange: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.space_screen)),
        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_2xl)),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { SettingsSubscreenHeader(titleRes = R.string.settings_library_preferences, onBack = onBack) }
        item {
            SettingsControlCard(icon = Icons.Outlined.Translate, titleRes = R.string.settings_primary_language_title) {
                Text(
                    text = stringResource(R.string.settings_primary_language_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LanguageCode.entries.forEach { language ->
                        FilterChip(
                            selected = settings.primaryLanguage == language.code,
                            onClick = { onPrimaryLanguageChange(language.code) },
                            label = { Text(stringResource(language.labelRes)) },
                        )
                    }
                }
            }
        }
        item {
            SettingsControlCard(icon = Icons.Outlined.GridView, titleRes = R.string.settings_default_library_view) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !settings.defaultGridMode,
                        onClick = { onDefaultGridModeChange(false) },
                        label = { Text(stringResource(R.string.action_list)) },
                    )
                    FilterChip(
                        selected = settings.defaultGridMode,
                        onClick = { onDefaultGridModeChange(true) },
                        label = { Text(stringResource(R.string.action_grid)) },
                    )
                }
            }
        }
        item {
            SettingsControlCard(icon = Icons.Outlined.Sort, titleRes = R.string.settings_default_sort) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BookSortCode.entries.forEach { sort ->
                        FilterChip(
                            selected = settings.defaultSort == sort,
                            onClick = { onDefaultSortChange(sort) },
                            label = { Text(stringResource(sort.labelRes)) },
                        )
                    }
                }
            }
        }
        item {
            SettingsControlCard(icon = Icons.Outlined.EmojiEvents, titleRes = R.string.settings_reading_goal_title) {
                Text(
                    text = stringResource(R.string.settings_reading_goal_desc, settings.readingGoal),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Slider(
                    value = settings.readingGoal.toFloat(),
                    onValueChange = { onReadingGoalChange(it.roundToInt()) },
                    valueRange = 1f..100f,
                    steps = 98,
                )
            }
        }
    }
}

@Composable
fun DataRecoverySettingsScreen(
    settings: LibrarySettings,
    onBack: () -> Unit,
    onExportJson: (Uri) -> Unit,
    onExportCsv: (Uri) -> Unit,
    onExportHtmlCatalog: (Uri) -> Unit,
    onExportPdfCatalog: (Uri) -> Unit,
    onExportCsvTemplate: (Uri) -> Unit,
    onImportJson: (Uri) -> Unit,
    onImportCsv: (Uri) -> Unit,
    onExportCompleteBackup: (Uri) -> Unit,
    onImportCompleteBackup: (Uri) -> Unit,
    onLoanRemindersEnabledChange: (Boolean) -> Unit,
    onLoanReminderLeadDaysChange: (Int) -> Unit,
    onBackupReminderDaysChange: (Int) -> Unit,
) {
    val context = LocalContext.current
    val jsonFilename = remember { "home_library_backup_${System.currentTimeMillis()}.json" }
    val csvFilename = remember { "home_library_books_${System.currentTimeMillis()}.csv" }
    val htmlFilename = remember { "home_library_catalog_${System.currentTimeMillis()}.html" }
    val pdfFilename = remember { "home_library_catalog_${System.currentTimeMillis()}.pdf" }
    val csvTemplateFilename = "home_library_import_template.csv"
    val completeBackupFilename = remember { "home_library_full_backup_${System.currentTimeMillis()}.zip" }

    val jsonExporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) onExportJson(uri)
    }
    val csvExporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri != null) onExportCsv(uri)
    }
    val htmlExporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/html")) { uri ->
        if (uri != null) onExportHtmlCatalog(uri)
    }
    val pdfExporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        if (uri != null) onExportPdfCatalog(uri)
    }
    val csvTemplateExporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri != null) onExportCsvTemplate(uri)
    }
    val jsonImporter = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) onImportJson(uri)
    }
    val csvImporter = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) onImportCsv(uri)
    }
    val completeBackupExporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
        if (uri != null) onExportCompleteBackup(uri)
    }
    val completeBackupImporter = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) onImportCompleteBackup(uri)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.space_screen)),
        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_2xl)),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { SettingsSubscreenHeader(titleRes = R.string.settings_data_recovery_title, onBack = onBack) }
        item {
            SettingsControlCard(icon = Icons.Outlined.CloudDone, titleRes = R.string.complete_backup_title) {
                Text(
                    text = stringResource(R.string.complete_backup_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 21.sp,
                )
                Text(
                    text = stringResource(R.string.settings_last_backup, settings.lastCompleteBackupEpochMillis.displayDate(context)),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 10.dp),
                )
                Slider(
                    value = settings.backupReminderDays.toFloat(),
                    onValueChange = { onBackupReminderDaysChange(it.roundToInt()) },
                    valueRange = 0f..90f,
                    steps = 89,
                )
                Text(
                    text = if (settings.backupReminderDays == 0) {
                        stringResource(R.string.settings_backup_reminder_disabled)
                    } else {
                        stringResource(R.string.settings_backup_reminder_desc, settings.backupReminderDays)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    OutlinedButton(onClick = { completeBackupImporter.launch(arrayOf("application/zip", "application/json", "text/*")) }) {
                        Text(stringResource(R.string.action_restore_complete_backup))
                    }
                    Button(onClick = { completeBackupExporter.launch(completeBackupFilename) }) {
                        Text(stringResource(R.string.action_export_complete_backup))
                    }
                }
            }
        }
        item {
            SettingsControlCard(icon = Icons.Outlined.FileDownload, titleRes = R.string.settings_data_exchange_title) {
                Text(
                    text = stringResource(R.string.settings_data_exchange_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 21.sp,
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = { jsonExporter.launch(jsonFilename) }, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.action_export_json))
                    }
                    OutlinedButton(onClick = { csvExporter.launch(csvFilename) }, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.action_export_csv))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = { jsonImporter.launch(arrayOf("application/json", "text/*")) }, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.action_import_json))
                    }
                    OutlinedButton(onClick = { csvImporter.launch(arrayOf("text/csv", "text/*")) }, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.action_import_csv))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = { htmlExporter.launch(htmlFilename) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.Code, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(6.dp))
                        Text(stringResource(R.string.action_export_html))
                    }
                    OutlinedButton(onClick = { pdfExporter.launch(pdfFilename) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(6.dp))
                        Text(stringResource(R.string.action_export_pdf))
                    }
                }
                OutlinedButton(
                    onClick = { csvTemplateExporter.launch(csvTemplateFilename) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                ) {
                    Icon(Icons.Outlined.Description, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text(stringResource(R.string.action_download_csv_template))
                }
            }
        }
        item {
            SettingsControlCard(icon = Icons.Outlined.Notifications, titleRes = R.string.settings_loan_reminders_title) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.settings_loan_reminders_enabled), style = MaterialTheme.typography.bodyLarge)
                        Text(stringResource(R.string.settings_loan_reminders_body), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = settings.loanRemindersEnabled, onCheckedChange = onLoanRemindersEnabledChange)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text(stringResource(R.string.settings_loan_reminder_lead_days), style = MaterialTheme.typography.labelMedium)
                Slider(
                    enabled = settings.loanRemindersEnabled,
                    value = settings.loanReminderLeadDays.toFloat(),
                    onValueChange = { onLoanReminderLeadDaysChange(it.roundToInt()) },
                    valueRange = 0f..14f,
                    steps = 13,
                )
                Text(
                    text = if (settings.loanReminderLeadDays == 0) {
                        stringResource(R.string.settings_loan_reminder_due_day)
                    } else {
                        stringResource(R.string.settings_loan_reminder_days_before, settings.loanReminderLeadDays)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreManagementScreen(
    settings: LibrarySettings,
    onBack: () -> Unit,
    onAddMainGenre: (String) -> Unit,
    onRemoveMainGenre: (String) -> Unit,
    onAddSubGenre: (String, String) -> Unit,
    onRemoveSubGenre: (String, String) -> Unit,
) {
    var selectedMainGenre by remember(settings.mainGenres) { mutableStateOf(settings.mainGenres.firstOrNull().orEmpty()) }
    val activeMainGenre = settings.mainGenres.firstOrNull { it.equals(selectedMainGenre, ignoreCase = true) }
        ?: settings.mainGenres.firstOrNull()
        ?: ""
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.space_screen)),
        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.space_2xl)),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { SettingsSubscreenHeader(titleRes = R.string.settings_genre_management_title, onBack = onBack) }
        item {
            SettingsControlCard(icon = Icons.Outlined.Category, titleRes = R.string.field_main_genre) {
                GenreManagementSection(
                    values = settings.mainGenres,
                    onAdd = onAddMainGenre,
                    onRemove = onRemoveMainGenre,
                )
            }
        }
        item {
            SettingsControlCard(icon = Icons.Outlined.Category, titleRes = R.string.field_sub_genre) {
                GenreParentDropdown(
                    selected = activeMainGenre,
                    options = settings.mainGenres,
                    onSelected = { selectedMainGenre = it },
                )
                if (activeMainGenre.isNotBlank()) {
                    GenreManagementSection(
                        values = settings.subGenresFor(activeMainGenre),
                        onAdd = { onAddSubGenre(activeMainGenre, it) },
                        onRemove = { onRemoveSubGenre(activeMainGenre, it) },
                    )
                } else {
                    Text(
                        text = stringResource(R.string.field_select_placeholder),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun GenreParentDropdown(
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        if (expanded) 180f else 0f,
        animationSpec = ExpressiveMotion.ExpressiveSpring,
        label = "dropdownArrowRotation"
    )
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            enabled = options.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
        ) {
            Text(
                text = selected.ifBlank { stringResource(R.string.field_select_placeholder) },
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
            )
            Icon(Icons.Outlined.ExpandMore, contentDescription = null, modifier = Modifier.rotate(rotation))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenreManagementSection(
    values: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
) {
    var newValue by remember { mutableStateOf("") }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        values.forEach { value ->
            ManagedGenreChip(label = value, onRemove = { onRemove(value) })
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = newValue,
            onValueChange = { newValue = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(R.string.genre_add_placeholder)) },
            singleLine = true,
            shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
        )
        Button(
            onClick = {
                onAdd(newValue)
                newValue = ""
            },
            enabled = newValue.isNotBlank(),
            shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
        ) {
            Text(stringResource(R.string.action_add))
        }
    }
}

@Composable
private fun ManagedGenreChip(label: String, onRemove: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
            // 36dp, not the usual 48dp touch target: this sits in a wrapping FlowRow of many
            // compact chips, and a full 48dp target would balloon every chip's height.
            IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.action_remove_genre, label),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun SettingsSubscreenHeader(
    @StringRes titleRes: Int,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.action_back))
        }
        Text(
            text = stringResource(titleRes),
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
private fun SettingsControlCard(
    icon: ImageVector,
    @StringRes titleRes: Int,
    content: @Composable ColumnScope.() -> Unit,
) {
    androidx.compose.material3.Surface(
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_prominent)),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SurfaceIcon(icon = icon)
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun SurfaceIcon(icon: ImageVector) {
    androidx.compose.material3.Surface(
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_md)),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(44.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}
