package com.sebastianvm.musicplayer.repository.playback

import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackWithQueueId
import com.sebastianvm.musicplayer.player.PlaybackInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface PlaybackManager {

    val playbackState: MutableStateFlow<PlaybackState>

    fun connectToService()
    fun disconnectFromService()
    fun play()
    fun pause()
    fun next()
    fun prev()

    fun playAllTracks(initialTrackIndex: Int = 0): Flow<PlaybackResult>
    fun playGenre(genreId: Long, initialTrackIndex: Int = 0): Flow<PlaybackResult>
    fun playAlbum(albumId: Long, initialTrackIndex: Int = 0): Flow<PlaybackResult>
    fun playArtist(artistId: Long): Flow<PlaybackResult>
    fun playPlaylist(playlistId: Long, initialTrackIndex: Int = 0): Flow<PlaybackResult>
    fun playSingleTrack(trackId: Long): Flow<PlaybackResult>

    fun moveQueueItem(previousIndex: Int, newIndex: Int)
    fun playQueueItem(index: Int)
    fun seekToTrackPosition(position: Long)
    fun addToQueue(tracks: List<Track>)
    fun getQueue(): Flow<List<TrackWithQueueId>>
    suspend fun modifySavedPlaybackInfo(newPlaybackInfo: PlaybackInfo)
    fun getSavedPlaybackInfo(): Flow<PlaybackInfo>

}

data class PlaybackState(
    val mediaItemMetadata: MediaItemMetadata?,
    val isPlaying: Boolean,
    val currentPlayTimeMs: Long
)

data class MediaItemMetadata(
    val title: String,
    val artists: String,
    val artworkUri: String,
    val trackDurationMs: Long
)
