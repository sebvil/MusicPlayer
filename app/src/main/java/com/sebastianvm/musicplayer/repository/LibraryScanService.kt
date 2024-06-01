package com.sebastianvm.musicplayer.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID
import com.sebastianvm.musicplayer.MusicPlayerApplication
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.features.main.MainActivity
import com.sebastianvm.musicplayer.repository.music.MusicRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LibraryScanService : Service() {

    private val dependencies by lazy {
        (application as MusicPlayerApplication).dependencies
    }

    private val mainDispatcher: CoroutineDispatcher by lazy {
        dependencies.dispatcherProvider.mainDispatcher
    }

    private var isRunning = false

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private val musicRepository: MusicRepository by lazy {
        dependencies.repositoryProvider.musicRepository
    }

    var job: Job? = null

    inner class MessageCallback(private val startId: Int) {
        fun updateProgress(progressMax: Int, currentProgress: Int, filePath: String) {
            notificationBuilder.setProgress(progressMax, currentProgress, false)
            notificationBuilder.setContentText("$currentProgress/$progressMax: $filePath")
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        }

        fun onFinished() {
            CoroutineScope(mainDispatcher).launch {
                job?.join()
                isRunning = false
                stopSelf(startId)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (isRunning) {
            stopSelf(startId)
            return START_NOT_STICKY
        } else {
            isRunning = true
        }

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE)
            }

        val stopServiceIntent = Intent(this, LibraryBroadcastReceiver::class.java).apply {
            action = STOP_SCAN_SERVICE
            putExtra(EXTRA_NOTIFICATION_ID, NOTIFICATION_ID)
        }
        val stopServicePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, stopServiceIntent, FLAG_IMMUTABLE)

        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Scanning library")
            .setSmallIcon(R.drawable.ic_album)
            .setContentIntent(pendingIntent)
            .setTicker("Library scan progress")
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(
                R.drawable.ic_close,
                getString(R.string.stop_scanning),
                stopServicePendingIntent
            )
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Library scan progress"
            val descriptionText = "Shows progress when scanning the library"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        job = CoroutineScope(mainDispatcher).launch {
            musicRepository.getMusic(MessageCallback(startId = startId))
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    companion object {
        private const val CHANNEL_ID = "com.sebastianvm.musicplayer.repository.SCAN"
        const val STOP_SCAN_SERVICE = "com.sebastianvm.musicplayer.repository.STOP_SCAN_SERVICE"
        private const val NOTIFICATION_ID = 0x1998
    }
}
