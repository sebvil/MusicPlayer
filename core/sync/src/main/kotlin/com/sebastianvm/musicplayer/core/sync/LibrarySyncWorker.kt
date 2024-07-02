package com.sebastianvm.musicplayer.core.sync

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.sebastianvm.musicplayer.core.data.music.MusicRepository
import com.sebastianvm.musicplayer.core.resources.RDrawable
import com.sebastianvm.musicplayer.core.resources.RString

class LibrarySyncWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val musicRepository: MusicRepository,
) : CoroutineWorker(context, workerParams) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {

        val progress = applicationContext.getString(RString.starting_sync)
        setForeground(createForegroundInfo(progress))

        musicRepository.getMusic()

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val title = applicationContext.getString(RString.scanning_library)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(RDrawable.ic_album)
                .setTicker(title)
                .setContentText(progress)
                .setOngoing(true)
                .addAction(
                    RDrawable.ic_close,
                    applicationContext.getString(RString.stop_scanning),
                    intent,
                )
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .build()

        val notificationId = 0x1999

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                notificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val name = applicationContext.getString(RString.library_scan_progress)
        val descriptionText =
            applicationContext.getString(RString.shows_progress_when_scanning_the_library)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
        // Register the channel with the system
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "com.sebastianvm.musicplayer.core.sync.SCAN"
    }
}
