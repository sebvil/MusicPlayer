package com.sebastianvm.musicplayer.repository.playback

import android.net.Uri
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.SavedPlaybackInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface MediaPlaybackRepository {

    val playbackState: MutableStateFlow<PlaybackState>
    val nowPlayingIndex: MutableStateFlow<Int>

    fun connectToService()
    fun disconnectFromService()
    fun play()
    fun pause()
    fun next()
    fun prev()
    fun playFromId(mediaId: String, mediaGroup: MediaGroup)
    fun moveQueueItem(previousIndex: Int, newIndex: Int)
    fun playQueueItem(index: Int)
    fun seekToTrackPosition(position: Long)
    suspend fun addToQueue(mediaIds: List<String>): Int
    fun getQueue(): Flow<List<String>>
    suspend fun modifySavedPlaybackInfo(transform: (savedPlaybackInfo: SavedPlaybackInfo) -> SavedPlaybackInfo)
    fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo>

}

data class PlaybackState(
    val mediaItemMetadata: MediaItemMetadata?,
    val isPlaying: Boolean,
    val currentPlayTimeMs: Long
)

data class MediaItemMetadata(
    val title: String,
    val artists: String,
    val artworkUri: Uri,
    val trackDurationMs: Long
)
