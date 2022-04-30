package com.sebastianvm.musicplayer.repository.playback

import androidx.media3.common.MediaItem
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.player.MediaPlaybackClient
import com.sebastianvm.musicplayer.player.PlaybackInfo
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import com.sebastianvm.musicplayer.util.extensions.toMediaItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    private fun playTracks(
        initialTrackIndex: Int = 0,
        tracksGetter: suspend () -> List<MediaItem>
    ): Flow<PlaybackResult> = flow {
        emit(PlaybackResult.Loading)
        val mediaItems = withContext(ioDispatcher) {
            delay(1000)
            tracksGetter()
        }
        if (mediaItems.isEmpty()) {
            emit(PlaybackResult.Error(R.string.error_collection_empty))
            return@flow
        }
        mediaPlaybackClient.playMediaItems(initialTrackIndex, mediaItems)
        emit(PlaybackResult.Success)
    }


    override fun playAllTracks(initialTrackIndex: Int): Flow<PlaybackResult> =
        playTracks(initialTrackIndex) {
            sortPreferencesRepository.getTracksListSortPreferences(TracksListType.ALL_TRACKS)
                .flatMapLatest { mediaSortPreferences ->
                    trackRepository.getAllTracks(mediaSortPreferences)
                }.first().map { it.toMediaItem() }
        }

    override fun playGenre(
        genreName: String,
        initialTrackIndex: Int,
    ): Flow<PlaybackResult> = playTracks(initialTrackIndex) {
        sortPreferencesRepository.getTracksListSortPreferences(TracksListType.GENRE, genreName)
            .flatMapLatest { mediaSortPreferences ->
                trackRepository.getTracksForGenre(genreName, mediaSortPreferences)
            }.first().map { it.toMediaItem() }

    }

    override fun playAlbum(albumId: String, initialTrackIndex: Int): Flow<PlaybackResult> =
        playTracks(initialTrackIndex) {
            trackRepository.getTracksForAlbum(albumId).first().map { it.toMediaItem() }
        }


    override fun playArtist(artistName: String): Flow<PlaybackResult> = playTracks {
        trackRepository.getTracksForArtist(artistName).first().map { it.toMediaItem() }
    }

    override fun playPlaylist(playlistName: String, initialTrackIndex: Int): Flow<PlaybackResult> =
        playTracks(initialTrackIndex) {
            trackRepository.getTracksForPlaylist(playlistName).first().map { it.toMediaItem() }
        }


    override fun playSingleTrack(trackId: String): Flow<PlaybackResult> =
        playTracks(initialTrackIndex = 0) {
            listOf(trackRepository.getTrack(trackId).first().track.toMediaItem())
        }


    override fun moveQueueItem(previousIndex: Int, newIndex: Int) {
        mediaPlaybackClient.moveQueueItem(previousIndex, newIndex)
    }

    override fun playQueueItem(index: Int) {
        mediaPlaybackClient.playQueueItem(index)
    }

    override suspend fun addToQueue(mediaIds: List<String>) {
        val tracks = withContext(ioDispatcher) {
            trackRepository.getTracks(tracksIds = mediaIds).first().map { it.toMediaItem() }
        }
        mediaPlaybackClient.addToQueue(tracks)
    }

    override fun getQueue(): Flow<List<Track>> {
        return playbackInfoDataSource.getSavedPlaybackInfo().map { it.queuedTracks }
    }

    override fun seekToTrackPosition(position: Long) {
        mediaPlaybackClient.seekToTrackPosition(position)
    }

    override suspend fun modifySavedPlaybackInfo(newPlaybackInfo: PlaybackInfo) {
        playbackInfoDataSource.modifySavedPlaybackInfo(newPlaybackInfo)
    }

    override fun getSavedPlaybackInfo(): Flow<PlaybackInfo> {
        return playbackInfoDataSource.getSavedPlaybackInfo()
    }
}
