package com.sebastianvm.musicplayer.repository.playback

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.sebastianvm.musicplayer.player.MediaGroup
import kotlinx.coroutines.flow.MutableStateFlow

interface PlaybackServiceRepository {

    val isConnected: MutableStateFlow<Boolean>

    val playbackState: MutableStateFlow<PlaybackStateCompat>

    val nowPlaying: MutableStateFlow<MediaMetadataCompat>

    val currentQueueId: MutableStateFlow<MediaGroup?>

    val mediaController: MediaControllerCompat

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback)

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback)

    fun getQueueId(mediaId: String): Long?

    fun sendCommand(command: String, parameters: Bundle?): Boolean
    fun sendCommand(
        command: String,
        parameters: Bundle?,
        resultCallback: ((Int, Bundle?) -> Unit)
    ) : Boolean

}

val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()
val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()

const val SORT_BY = "SORT_BY"
const val MEDIA_GROUP = "com.sebastianvm.musicplayer.player.MEDIA_GROUP"
const val COMMAND_SEEK_TO_MEDIA_ITEM = "com.sebastianvm.player.COMMAND_SEEK_TO_MEDIA_ITEM"
const val EXTRA_MEDIA_INDEX = "com.sebastianvm.player.EXTRA_MEDIA_INDEX"
const val EXTRA_TO_INDEX = "com.sebastianvm.player.EXTRA_TO_INDEX"
