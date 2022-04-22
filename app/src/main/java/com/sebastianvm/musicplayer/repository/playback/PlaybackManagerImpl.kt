package com.sebastianvm.musicplayer.repository.playback

import androidx.media3.common.MediaItem
import com.sebastianvm.musicplayer.player.MediaPlaybackClient
import com.sebastianvm.musicplayer.player.SavedPlaybackInfo
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import com.sebastianvm.musicplayer.util.extensions.toMediaItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class PlaybackManagerImpl @Inject constructor(
    private val mediaPlaybackClient: MediaPlaybackClient,
    private val playbackInfoDataSource: PlaybackInfoDataSource,
    private val trackRepository: TrackRepository,
    private val sortPreferencesRepository: SortPreferencesRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : PlaybackManager {
    override val playbackState: MutableStateFlow<PlaybackState> = mediaPlaybackClient.playbackState
    override val nowPlayingIndex: MutableStateFlow<Int> = mediaPlaybackClient.currentIndex

    override fun connectToService() {
        mediaPlaybackClient.initializeController()
    }

    override fun disconnectFromService() {
        mediaPlaybackClient.releaseController()
    }


    override fun play() {
        mediaPlaybackClient.play()
    }

    override fun pause() {
        mediaPlaybackClient.pause()
    }

    override fun next() {
        mediaPlaybackClient.next()
    }

    override fun prev() {
        mediaPlaybackClient.prev()
    }

    // TODO use flow and Data/Loading/Error pattern to handle this
    private suspend fun playTracks(
        startingTrackId: String? = null,
        tracksGetter: suspend () -> List<MediaItem>
    ) {
        val mediaItems = withContext(ioDispatcher) {
            tracksGetter()
        }
        val startingTrackIdNonNull = startingTrackId ?: mediaItems.firstOrNull()?.mediaId ?: return
        mediaPlaybackClient.playMediaItems(startingTrackIdNonNull, mediaItems)
    }

    override suspend fun playAllTracks(
        startingTrackId: String
    ) {
        playTracks(startingTrackId) {
            sortPreferencesRepository.getTracksListSortPreferences(TracksListType.ALL_TRACKS)
                .flatMapLatest { mediaSortPreferences ->
                    trackRepository.getAllTracks(mediaSortPreferences)
                }.first().map { it.toMediaItem() }
        }
    }

    override suspend fun playGenre(
        genreName: String,
        startingTrackId: String?,
    ) {
        playTracks(startingTrackId) {
            sortPreferencesRepository.getTracksListSortPreferences(TracksListType.GENRE, genreName)
                .flatMapLatest { mediaSortPreferences ->
                    trackRepository.getTracksForGenre(genreName, mediaSortPreferences)
                }.first().map { it.toMediaItem() }
        }
    }

    override suspend fun playAlbum(albumId: String, startingTrackId: String?) {
        playTracks(startingTrackId) {
            trackRepository.getTracksForAlbum(albumId).first().map { it.toMediaItem() }
        }
    }

    override suspend fun playArtist(artistName: String) {
        playTracks {
            trackRepository.getTracksForArtist(artistName).first().map { it.toMediaItem() }
        }
    }

    override suspend fun playPlaylist(playlistName: String, startingTrackId: String?) {
        playTracks(startingTrackId) {
            trackRepository.getTracksForPlaylist(playlistName).first().map { it.toMediaItem() }
        }
    }

    override suspend fun playSingleTrack(trackId: String) {
        playTracks(trackId) {
            listOf(trackRepository.getTrack(trackId).first().track.toMediaItem())
        }
    }


    override fun moveQueueItem(previousIndex: Int, newIndex: Int) {
        mediaPlaybackClient.moveQueueItem(previousIndex, newIndex)
    }

    override fun playQueueItem(index: Int) {
        mediaPlaybackClient.playQueueItem(index)
    }

    override suspend fun addToQueue(mediaIds: List<String>): Int {
        return mediaPlaybackClient.addToQueue(mediaIds)
    }

    override fun getQueue(): Flow<List<String>> {
        return mediaPlaybackClient.queue
    }

    override fun seekToTrackPosition(position: Long) {
        mediaPlaybackClient.seekToTrackPosition(position)
    }

    override suspend fun modifySavedPlaybackInfo(transform: (savedPlaybackInfo: SavedPlaybackInfo) -> SavedPlaybackInfo) {
        playbackInfoDataSource.modifySavedPlaybackInfo(transform)
    }

    override fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo> {
        return playbackInfoDataSource.getSavedPlaybackInfo()
    }
}
