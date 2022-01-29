package com.sebastianvm.musicplayer.repository.playback

import androidx.media3.common.MediaMetadata
import com.sebastianvm.musicplayer.player.MediaGroup
import kotlinx.coroutines.flow.MutableStateFlow

interface MediaPlaybackRepository {

    val nowPlaying: MutableStateFlow<MediaMetadata?>
    val playbackState: MutableStateFlow<PlaybackState>

    fun connectToService()
    fun disconnectFromService()
    fun play()
    fun pause()
    fun next()
    fun prev()
    fun playFromId(mediaId: String, mediaGroup: MediaGroup)
    fun moveQueueItem(previousIndex: Int, newIndex: Int)
    fun playQueueItem(index: Int)

}

data class PlaybackState(val isPlaying: Boolean, val currentPlayTimeMs: Long)
