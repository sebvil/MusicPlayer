package com.sebastianvm.musicplayer.repository.playback

import com.sebastianvm.fakegen.FakeCommandMethod
import com.sebastianvm.fakegen.FakeQueryMethod
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.PlaybackInfo
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface PlaybackManager {

    @FakeQueryMethod
    fun getPlaybackState(): Flow<PlaybackState>

    @FakeCommandMethod
    fun connectToService()

    @FakeCommandMethod
    fun disconnectFromService()

    @FakeCommandMethod
    fun togglePlay()

    @FakeCommandMethod
    fun next()

    @FakeCommandMethod
    fun prev()

    @FakeQueryMethod
    suspend fun playMedia(mediaGroup: MediaGroup, initialTrackIndex: Int = 0)

    @FakeCommandMethod
    fun seekToTrackPosition(position: Long)

    @FakeCommandMethod
    fun addToQueue(tracks: List<Track>)

    @FakeCommandMethod
    suspend fun modifySavedPlaybackInfo(newPlaybackInfo: PlaybackInfo)

    @FakeQueryMethod
    fun getSavedPlaybackInfo(): Flow<PlaybackInfo>
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
