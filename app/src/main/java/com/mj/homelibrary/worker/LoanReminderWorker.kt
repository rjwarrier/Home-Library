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
import androidx.work.WorkerParameters
import com.mj.homelibrary.HomeLibraryApplication
import com.mj.homelibrary.R
import kotlinx.coroutines.flow.first
import java.text.DateFormat
import java.util.Date

class LoanReminderWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val app = applicationContext as HomeLibraryApplication
        val loanId = inputData.getLong(KEY_LOAN_ID, 0L)
        val bookId = inputData.getLong(KEY_BOOK_ID, 0L)
        val loan = app.database.loanDao().get(loanId) ?: return Result.success()
        if (loan.actualReturnDateEpochMillis != null) return Result.success()
        val book = app.database.bookDao().observeBook(bookId).first() ?: return Result.success()

        createChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val dueDate = loan.expectedReturnDateEpochMillis?.let {
            DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(it))
        }.orEmpty()
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.notification_due_title))
            .setContentText(applicationContext.getString(R.string.notification_due_body, book.title, dueDate))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(loanId.toInt(), notification)
        return Result.success()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            applicationContext.getString(R.string.notification_channel_loans),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = applicationContext.getString(R.string.notification_channel_loans_description)
        }
        applicationContext.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val KEY_LOAN_ID = "loan_id"
        const val KEY_BOOK_ID = "book_id"
        private const val CHANNEL_ID = "loan_reminders"
    }
}
