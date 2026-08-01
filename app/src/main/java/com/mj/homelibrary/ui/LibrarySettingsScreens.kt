package com.mj.homelibrary.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.mj.homelibrary.data.LibrarySettings
import kotlin.math.roundToInt

@Composable
fun LibraryPreferencesScreen(
    settings: LibrarySettings,
    onBack: () -> Unit,
    onDefaultGridModeChange: (Boolean) -> Unit,
    onDefaultSortChange: (BookSortCode) -> Unit,
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
    }
}

@Composable
fun DataRecoverySettingsScreen(
    settings: LibrarySettings,
    onBack: () -> Unit,
    onExportJson: (Uri) -> Unit,
    onExportCsv: (Uri) -> Unit,
    onImportJson: (Uri) -> Unit,
    onImportCsv: (Uri) -> Unit,
    onExportCompleteBackup: (Uri) -> Unit,
    onImportCompleteBackup: (Uri) -> Unit,
    onLoanRemindersEnabledChange: (Boolean) -> Unit,
    onLoanReminderLeadDaysChange: (Int) -> Unit,
    onBackupReminderDaysChange: (Int) -> Unit,
) {
    val context = LocalContext.current
    val jsonFilename = stringResource(R.string.backup_json_filename)
    val csvFilename = stringResource(R.string.backup_csv_filename)
    val completeBackupFilename = stringResource(R.string.backup_complete_filename)
    val jsonExporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) onExportJson(uri)
    }
    val csvExporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri != null) onExportCsv(uri)
    }
    val jsonImporter = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) onImportJson(uri)
    }
    val csvImporter = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) onImportCsv(uri)
    }
    val completeBackupExporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
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
                    OutlinedButton(onClick = { completeBackupImporter.launch(arrayOf("application/json", "text/*")) }) {
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

@Composable
private fun SettingsSubscreenHeader(
    @StringRes titleRes: Int,
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

@Composable
private fun SettingsControlCard(
    icon: ImageVector,
    @StringRes titleRes: Int,
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            SurfaceIcon(icon = icon)
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(titleRes), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Box(modifier = Modifier.padding(top = 10.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
                }
            }
        }
    }
}

@Composable
private fun SurfaceIcon(icon: ImageVector) {
    androidx.compose.material3.Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(44.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}
