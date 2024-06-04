package com.sebastianvm.musicplayer.repository.playback

import com.sebastianvm.musicplayer.player.MediaGroup
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface PlaybackManager {

    fun getPlaybackState(): Flow<PlaybackState>

    fun connectToService()

    fun disconnectFromService()

    fun togglePlay()

    fun next()

    fun prev()

    suspend fun playMedia(mediaGroup: MediaGroup, initialTrackIndex: Int = 0)

    fun playQueueItem(index: Int)

    fun seekToTrackPosition(position: Long)

    suspend fun addToQueue(mediaGroup: MediaGroup)
}

sealed interface PlaybackState

data class TrackPlayingState(
    val trackInfo: TrackInfo,
    val isPlaying: Boolean,
    val currentTrackProgress: Duration
) : PlaybackState

data object NotPlayingState : PlaybackState

data class TrackInfo(
    val title: String,
    val artists: String,
    val artworkUri: String,
    val trackLength: Duration
)
