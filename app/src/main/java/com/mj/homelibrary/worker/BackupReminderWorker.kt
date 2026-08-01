package com.mj.homelibrary.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.mj.homelibrary.HomeLibraryApplication
import com.mj.homelibrary.R
import java.util.concurrent.TimeUnit

class BackupReminderWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val app = applicationContext as HomeLibraryApplication
        val settings = app.librarySettingsRepository.settings.value
        if (settings.backupReminderDays <= 0) return Result.success()

        val lastBackup = settings.lastCompleteBackupEpochMillis
        val dueMillis = TimeUnit.DAYS.toMillis(settings.backupReminderDays.toLong())
        val overdue = lastBackup == null || System.currentTimeMillis() - lastBackup >= dueMillis
        if (!overdue) return Result.success()

        createChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.notification_backup_title))
            .setContentText(applicationContext.getString(R.string.notification_backup_body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
        return Result.success()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            applicationContext.getString(R.string.notification_channel_backup),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = applicationContext.getString(R.string.notification_channel_backup_description)
        }
        applicationContext.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "backup_reminders"
        private const val UNIQUE_WORK_NAME = "backup_reminder_check"
        private const val NOTIFICATION_ID = 9001

        fun reschedule(workManager: WorkManager, reminderDays: Int) {
            if (reminderDays <= 0) {
                workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
                return
            }
            val intervalDays = reminderDays.coerceAtLeast(1).toLong()
            val request = PeriodicWorkRequestBuilder<BackupReminderWorker>(intervalDays, TimeUnit.DAYS)
                .build()
            workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
        }
    }
}
