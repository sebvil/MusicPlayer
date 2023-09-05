package com.sebastianvm.musicplayer.repository.playback

import com.sebastianvm.fakegen.FakeCommandMethod
import com.sebastianvm.fakegen.FakeQueryMethod
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackWithQueueId
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.PlaybackInfo
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface PlaybackManager {

    @FakeQueryMethod
    fun getPlaybackState(): Flow<Pair<PlaybackState, Duration>>

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
    fun playAllTracks(initialTrackIndex: Int = 0): Flow<PlaybackResult>

    @FakeQueryMethod
    fun playGenre(genreId: Long, initialTrackIndex: Int = 0): Flow<PlaybackResult>

    @FakeQueryMethod
    fun playAlbum(albumId: Long, initialTrackIndex: Int = 0): Flow<PlaybackResult>

    @FakeQueryMethod
    fun playArtist(artistId: Long): Flow<PlaybackResult>

    @FakeQueryMethod
    fun playPlaylist(playlistId: Long, initialTrackIndex: Int = 0): Flow<PlaybackResult>

    @FakeQueryMethod
    fun playSingleTrack(trackId: Long): Flow<PlaybackResult>

    @FakeQueryMethod
    fun playMedia(mediaGroup: MediaGroup, initialTrackIndex: Int = 0): Flow<PlaybackResult>

    @FakeCommandMethod
    fun moveQueueItem(previousIndex: Int, newIndex: Int)

    @FakeCommandMethod
    fun playQueueItem(index: Int)

    @FakeCommandMethod
    fun seekToTrackPosition(position: Long)

    @FakeCommandMethod
    fun addToQueue(tracks: List<Track>)

    @FakeQueryMethod
    fun getQueue(): Flow<List<TrackWithQueueId>>

    @FakeCommandMethod
    suspend fun modifySavedPlaybackInfo(newPlaybackInfo: PlaybackInfo)

    @FakeQueryMethod
    fun getSavedPlaybackInfo(): Flow<PlaybackInfo>
}

sealed interface PlaybackState

data class TrackPlayingState(
    val trackInfo: TrackInfo,
    val isPlaying: Boolean,
) : PlaybackState

data object NotPlayingState : PlaybackState

data class TrackInfo(
    val title: String,
    val artists: String,
    val artworkUri: String,
    val trackLength: Duration
)
