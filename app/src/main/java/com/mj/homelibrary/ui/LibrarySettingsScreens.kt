package com.mj.homelibrary.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.mj.homelibrary.R
import com.mj.homelibrary.data.BookSortCode
import com.mj.homelibrary.data.LanguageCode
import com.mj.homelibrary.data.LibrarySettings
import com.mj.homelibrary.data.remote.OcrLanguageManager
import com.mj.homelibrary.data.remote.OcrLanguagePack
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LibraryPreferencesScreen(
    settings: LibrarySettings,
    onBack: () -> Unit,
    onDefaultGridModeChange: (Boolean) -> Unit,
    onDefaultSortChange: (BookSortCode) -> Unit,
    onReadingGoalChange: (Int) -> Unit,
    onPrimaryLanguageChange: (String) -> Unit,
    onShareCatalogChange: (Boolean) -> Unit,
) {
    SettingsPage(titleRes = R.string.settings_library_preferences, onBack = onBack) {
        item {
            SettingsGroup(label = stringResource(R.string.settings_primary_language_title)) {
                SettingsBody(stringResource(R.string.settings_primary_language_body))
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
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
            SettingsGroup(label = stringResource(R.string.settings_default_library_view)) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    SegmentedButton(
                        selected = !settings.defaultGridMode,
                        onClick = { onDefaultGridModeChange(false) },
                        shape = SegmentedButtonDefaults.itemShape(0, 2),
                        icon = { SegmentedButtonDefaults.Icon(active = !settings.defaultGridMode, inactiveContent = { Icon(Icons.Outlined.ViewList, null, Modifier.size(SegmentedButtonDefaults.IconSize)) }) },
                    ) { Text(stringResource(R.string.action_list)) }
                    SegmentedButton(
                        selected = settings.defaultGridMode,
                        onClick = { onDefaultGridModeChange(true) },
                        shape = SegmentedButtonDefaults.itemShape(1, 2),
                        icon = { SegmentedButtonDefaults.Icon(active = settings.defaultGridMode, inactiveContent = { Icon(Icons.Outlined.GridView, null, Modifier.size(SegmentedButtonDefaults.IconSize)) }) },
                    ) { Text(stringResource(R.string.action_grid)) }
                }
            }
        }
        item {
            SettingsGroup(label = stringResource(R.string.settings_default_sort)) {
                BookSortCode.entries.forEach { sort ->
                    SettingsRadioRow(
                        title = stringResource(sort.labelRes),
                        selected = settings.defaultSort == sort,
                        onClick = { onDefaultSortChange(sort) },
                    )
                }
            }
        }
        item {
            SettingsGroup {
                SettingsSwitchRow(
                    icon = Icons.Outlined.Share,
                    title = stringResource(R.string.settings_share_catalog_enabled),
                    supporting = stringResource(R.string.settings_share_catalog_body),
                    checked = settings.shareCatalogWithOtherApps,
                    onCheckedChange = onShareCatalogChange,
                )
            }
        }
        item {
            SettingsGroup {
                SettingsSliderRow(
                    title = stringResource(R.string.settings_reading_goal_title),
                    valueLabel = settings.readingGoal.toString(),
                    value = settings.readingGoal.toFloat(),
                    valueRange = 1f..100f,
                    onValueChange = { onReadingGoalChange(it.roundToInt()) },
                    supporting = stringResource(R.string.settings_reading_goal_desc, settings.readingGoal),
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

    SettingsPage(titleRes = R.string.settings_data_recovery_title, onBack = onBack) {
        item {
            SettingsGroup(label = stringResource(R.string.complete_backup_title)) {
                SettingsRow(
                    icon = Icons.Outlined.CloudDone,
                    title = stringResource(R.string.settings_last_backup, settings.lastCompleteBackupEpochMillis.displayDate(context)),
                    supporting = stringResource(R.string.complete_backup_body),
                )
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = { completeBackupExporter.launch(completeBackupFilename) },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(stringResource(R.string.action_export_complete_backup)) }
                    OutlinedButton(
                        onClick = { completeBackupImporter.launch(arrayOf("application/zip", "application/json", "text/*")) },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(stringResource(R.string.action_restore_complete_backup)) }
                }
                SettingsDivider()
                SettingsSliderRow(
                    title = stringResource(R.string.settings_backup_reminder_title),
                    valueLabel = if (settings.backupReminderDays == 0) stringResource(R.string.settings_reminder_off) else "${settings.backupReminderDays}",
                    value = settings.backupReminderDays.toFloat(),
                    valueRange = 0f..90f,
                    onValueChange = { onBackupReminderDaysChange(it.roundToInt()) },
                    supporting = if (settings.backupReminderDays == 0) {
                        stringResource(R.string.settings_backup_reminder_disabled)
                    } else {
                        stringResource(R.string.settings_backup_reminder_desc, settings.backupReminderDays)
                    },
                )
            }
        }
        item {
            SettingsGroup(label = stringResource(R.string.settings_data_exchange_title)) {
                SettingsBody(stringResource(R.string.settings_data_exchange_body))
                SettingsRow(
                    icon = Icons.Outlined.FileDownload,
                    title = stringResource(R.string.action_export_json),
                    onClick = { jsonExporter.launch(jsonFilename) },
                )
                SettingsRow(
                    icon = Icons.Outlined.FileDownload,
                    title = stringResource(R.string.action_export_csv),
                    onClick = { csvExporter.launch(csvFilename) },
                )
                SettingsRow(
                    icon = Icons.Outlined.Code,
                    title = stringResource(R.string.action_export_html),
                    onClick = { htmlExporter.launch(htmlFilename) },
                )
                SettingsRow(
                    icon = Icons.Outlined.PictureAsPdf,
                    title = stringResource(R.string.action_export_pdf),
                    onClick = { pdfExporter.launch(pdfFilename) },
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Outlined.FileUpload,
                    title = stringResource(R.string.action_import_json),
                    onClick = { jsonImporter.launch(arrayOf("application/json", "text/*")) },
                )
                SettingsRow(
                    icon = Icons.Outlined.FileUpload,
                    title = stringResource(R.string.action_import_csv),
                    onClick = { csvImporter.launch(arrayOf("text/csv", "text/*")) },
                )
                SettingsRow(
                    icon = Icons.Outlined.Description,
                    title = stringResource(R.string.action_download_csv_template),
                    onClick = { csvTemplateExporter.launch(csvTemplateFilename) },
                )
            }
        }
        item {
            SettingsGroup(label = stringResource(R.string.settings_loan_reminders_title)) {
                SettingsSwitchRow(
                    title = stringResource(R.string.settings_loan_reminders_enabled),
                    supporting = stringResource(R.string.settings_loan_reminders_body),
                    checked = settings.loanRemindersEnabled,
                    onCheckedChange = onLoanRemindersEnabledChange,
                )
                SettingsDivider()
                SettingsSliderRow(
                    title = stringResource(R.string.settings_loan_reminder_lead_days),
                    valueLabel = settings.loanReminderLeadDays.toString(),
                    enabled = settings.loanRemindersEnabled,
                    value = settings.loanReminderLeadDays.toFloat(),
                    valueRange = 0f..14f,
                    steps = 13,
                    onValueChange = { onLoanReminderLeadDaysChange(it.roundToInt()) },
                    supporting = if (settings.loanReminderLeadDays == 0) {
                        stringResource(R.string.settings_loan_reminder_due_day)
                    } else {
                        stringResource(R.string.settings_loan_reminder_days_before, settings.loanReminderLeadDays)
                    },
                )
            }
        }
    }
}

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
    SettingsPage(titleRes = R.string.settings_genre_management_title, onBack = onBack) {
        item {
            SettingsGroup(label = stringResource(R.string.field_main_genre)) {
                GenreManagementSection(
                    values = settings.mainGenres,
                    onAdd = onAddMainGenre,
                    onRemove = onRemoveMainGenre,
                )
            }
        }
        item {
            SettingsGroup(label = stringResource(R.string.field_sub_genre)) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
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
                            padded = false,
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.field_select_placeholder),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenreParentDropdown(
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (options.isNotEmpty()) expanded = it },
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            enabled = options.isNotEmpty(),
            placeholder = { Text(stringResource(R.string.field_select_placeholder)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
    padded: Boolean = true,
) {
    var newValue by remember { mutableStateOf("") }
    val submit = {
        if (newValue.isNotBlank()) {
            onAdd(newValue)
            newValue = ""
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth().then(if (padded) Modifier.padding(16.dp) else Modifier),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            values.forEach { value ->
                InputChip(
                    selected = false,
                    onClick = { onRemove(value) },
                    label = { Text(value) },
                    trailingIcon = {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.action_remove_genre, value),
                            modifier = Modifier.size(18.dp),
                        )
                    },
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            OutlinedTextField(
                value = newValue,
                onValueChange = { newValue = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.genre_add_placeholder)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submit() }),
            )
            FilledTonalButton(onClick = { submit() }, enabled = newValue.isNotBlank()) {
                Text(stringResource(R.string.action_add))
            }
        }
    }
}

@Composable
fun OcrLanguagePacksScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var installedPacks by remember { mutableStateOf(OcrLanguageManager.getInstalledLanguages(context)) }
    var downloadingCode by remember { mutableStateOf<String?>(null) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var packPendingRemoval by remember { mutableStateOf<OcrLanguagePack?>(null) }

    SettingsPage(titleRes = R.string.settings_ocr_language_packs_title, onBack = onBack) {
        item {
            Text(
                text = stringResource(R.string.settings_ocr_language_packs_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        item {
            SettingsGroup {
                OcrLanguageManager.availableLanguages.forEachIndexed { index, pack ->
                    val isInstalled = installedPacks.any { it.code == pack.code }
                    val isDownloading = downloadingCode == pack.code
                    if (index > 0) SettingsDivider()
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text(pack.nativeName, style = MaterialTheme.typography.titleMedium) },
                        supportingContent = {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("${pack.englishName} · ${pack.sizeMb} MB", style = MaterialTheme.typography.bodyMedium)
                                if (isDownloading) {
                                    LinearProgressIndicator(progress = { downloadProgress }, modifier = Modifier.fillMaxWidth())
                                    Text(
                                        stringResource(R.string.ocr_lang_downloading, (downloadProgress * 100).toInt()),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                } else if (isInstalled) {
                                    Text(
                                        stringResource(R.string.ocr_lang_installed),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        },
                        trailingContent = {
                            when {
                                isInstalled -> IconButton(onClick = { packPendingRemoval = pack }) {
                                    Icon(
                                        Icons.Outlined.Delete,
                                        contentDescription = stringResource(R.string.ocr_lang_delete),
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                }
                                isDownloading -> CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                                else -> FilledTonalButton(
                                    enabled = downloadingCode == null,
                                    onClick = {
                                        downloadingCode = pack.code
                                        downloadProgress = 0f
                                        coroutineScope.launch {
                                            val result = OcrLanguageManager.downloadLanguagePack(context, pack.code) { progress ->
                                                downloadProgress = progress
                                            }
                                            downloadingCode = null
                                            if (result.isSuccess) {
                                                installedPacks = OcrLanguageManager.getInstalledLanguages(context)
                                            }
                                        }
                                    },
                                ) {
                                    Text(stringResource(R.string.ocr_lang_download, pack.sizeMb))
                                }
                            }
                        },
                    )
                }
            }
        }
    }

    packPendingRemoval?.let { pack ->
        AlertDialog(
            onDismissRequest = { packPendingRemoval = null },
            title = { Text(stringResource(R.string.ocr_lang_delete)) },
            text = { Text(stringResource(R.string.ocr_lang_delete_confirm, pack.englishName)) },
            confirmButton = {
                TextButton(onClick = {
                    OcrLanguageManager.deleteLanguagePack(context, pack.code)
                    installedPacks = OcrLanguageManager.getInstalledLanguages(context)
                    packPendingRemoval = null
                }) { Text(stringResource(R.string.ocr_lang_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { packPendingRemoval = null }) { Text(stringResource(R.string.action_cancel)) }
            },
        )
    }
}
