package com.sebastianvm.musicplayer.repository.playback

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.sebastianvm.musicplayer.player.MediaGroup
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaybackServiceRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    serviceComponent: ComponentName
) : PlaybackServiceRepository {
    override val isConnected = MutableStateFlow(false)

    override val playbackState = MutableStateFlow(EMPTY_PLAYBACK_STATE)

    override val nowPlaying = MutableStateFlow(NOTHING_PLAYING)

    override val currentQueueId: MutableStateFlow<MediaGroup?> = MutableStateFlow(null)

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(
        context,
        serviceComponent,
        mediaBrowserConnectionCallback, null
    ).apply { connect() }

    override lateinit var mediaController: MediaControllerCompat

    override val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    override fun getQueueId(mediaId: String): Long? {
        return mediaController.queue.find { it.description.mediaId == mediaId }?.queueId
    }

    override fun sendCommand(command: String, parameters: Bundle?) =
        sendCommand(command, parameters) { _, _ -> }

    override fun sendCommand(
        command: String,
        parameters: Bundle?,
        resultCallback: ((Int, Bundle?) -> Unit)
    ) = if (mediaBrowser.isConnected) {
        mediaController.sendCommand(
            command,
            parameters,
            object : ResultReceiver(Handler(Looper.getMainLooper())) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                    resultCallback(resultCode, resultData)
                }
            })
        true
    } else {
        false
    }

    private inner class MediaBrowserConnectionCallback(@ApplicationContext private val context: Context) :
        MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            CoroutineScope(Dispatchers.Main).launch {
                isConnected.emit(true)
            }
        }

        override fun onConnectionSuspended() {
            CoroutineScope(Dispatchers.Main).launch {
                isConnected.emit(true)
            }
        }


        override fun onConnectionFailed() {
            CoroutineScope(Dispatchers.Main).launch {
                isConnected.emit(false)
            }
        }
    }


    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            playbackState.value = state ?: EMPTY_PLAYBACK_STATE
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            // When ExoPlayer stops we will receive a callback with "empty" metadata. This is a
            // metadata object which has been instantiated with default values. The default value
            // for media ID is null so we assume that if this value is null we are not playing
            // anything.
            nowPlaying.value =
                if (metadata?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) == null) {
                    NOTHING_PLAYING
                } else {
                    metadata
                }

        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
        }

        /**
         * Normally if a [MediaBrowserServiceCompat] drops its connection the callback comes via
         * [MediaControllerCompat.Callback] (here). But since other connection status events
         * are sent to [MediaBrowserCompat.ConnectionCallback], we catch the disconnect here and
         * send it on to the other callback.
         */
        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }

        override fun onExtrasChanged(extras: Bundle?) {
            currentQueueId.value = extras?.getParcelable(MEDIA_GROUP)
        }
    }
}
