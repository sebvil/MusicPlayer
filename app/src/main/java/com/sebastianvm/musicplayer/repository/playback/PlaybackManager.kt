package com.sebastianvm.musicplayer.repository.playback

import android.net.Uri
import com.sebastianvm.musicplayer.player.SavedPlaybackInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface PlaybackManager {

    val playbackState: MutableStateFlow<PlaybackState>
    val nowPlayingIndex: MutableStateFlow<Int>

    fun connectToService()
    fun disconnectFromService()
    fun play()
    fun pause()
    fun next()
    fun prev()

    suspend fun playAllTracks(startingTrackId: String)
    suspend fun playGenre(genreName: String, startingTrackId: String? = null)
    suspend fun playAlbum(albumId: String, startingTrackId: String? = null)
    suspend fun playArtist(artistName: String)
    suspend fun playPlaylist(playlistName: String, startingTrackId: String? = null)
    suspend fun playSingleTrack(trackId: String)

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
