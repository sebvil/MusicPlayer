package com.sebastianvm.musicplayer.player

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Size
import androidx.annotation.RequiresApi
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.sebastianvm.commons.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileNotFoundException


class PlaybackNotificationManager(
    @ApplicationContext private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    playerNotificationListener: PlayerNotificationManager.NotificationListener
) {

    private val playerNotificationManager: PlayerNotificationManager


    init {
        val mediaController = MediaControllerCompat(context, sessionToken)

        playerNotificationManager = PlayerNotificationManager.Builder(
            context,
            NOTIFICATION_ID,
            CHANNEL_ID
        ).setMediaDescriptionAdapter(DescriptionAdapter(mediaControllerCompat = mediaController))
            .setNotificationListener(playerNotificationListener)
            .setChannelNameResourceId(R.string.library)
            .build().apply {
                setMediaSessionToken(sessionToken)
            }
    }

    fun hideNotification() {
        playerNotificationManager.setPlayer(null)
    }

    fun showNotificationForPlayer(player: Player) {
        playerNotificationManager.setPlayer(player)
    }

    private inner class DescriptionAdapter(val mediaControllerCompat: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence {
            return "Title"
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return mediaControllerCompat.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return "Current content text"
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val uri = player.currentMediaItem?.mediaMetadata?.artworkUri
            return uri?.let {
                try {
                    context.contentResolver.loadThumbnail(uri, Size(500,500), null)
                } catch (e: FileNotFoundException) {
                    null
                }
            }
        }

    }

    companion object {
        const val CHANNEL_ID = "com.sebastianvm.musicplayer.player.NOW_PLAYING"
        const val NOTIFICATION_ID = 0x1999
    }

}