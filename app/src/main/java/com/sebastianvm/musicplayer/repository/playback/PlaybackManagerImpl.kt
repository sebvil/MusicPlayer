package com.sebastianvm.musicplayer.repository.playback

import androidx.media3.common.MediaItem
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackWithQueueId
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaPlaybackClient
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaybackManagerImpl @Inject constructor(
    private val mediaPlaybackClient: MediaPlaybackClient,
    private val playbackInfoRepository: PlaybackInfoRepository,
    private val trackRepository: TrackRepository,
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
            trackRepository.getAllTracks().first().map { it.toMediaItem() }
        }

    override fun playGenre(
        genreId: Long,
        initialTrackIndex: Int,
    ): Flow<PlaybackResult> = playTracks(initialTrackIndex) {
        trackRepository.getTracksForGenre(genreId).first().map { it.toMediaItem() }
    }

    override fun playAlbum(albumId: Long, initialTrackIndex: Int): Flow<PlaybackResult> =
        playTracks(initialTrackIndex) {
            trackRepository.getTracksForAlbum(albumId).first().map { it.toMediaItem() }
        }


    override fun playArtist(artistId: Long): Flow<PlaybackResult> = playTracks {
        trackRepository.getTracksForArtist(artistId).first().map { it.toMediaItem() }
    }

    override fun playPlaylist(playlistId: Long, initialTrackIndex: Int): Flow<PlaybackResult> =
        playTracks(initialTrackIndex) {
            trackRepository.getTracksForPlaylist(playlistId).first().map { it.toMediaItem() }
        }


    override fun playSingleTrack(trackId: Long): Flow<PlaybackResult> =
        playTracks(initialTrackIndex = 0) {
            listOf(trackRepository.getTrack(trackId).first().track.toMediaItem())
        }

    override fun playMedia(mediaGroup: MediaGroup, initialTrackIndex: Int): Flow<PlaybackResult> {
        return playTracks(initialTrackIndex) {
            when (mediaGroup) {
                is MediaGroup.AllTracks -> trackRepository.getAllTracks()
                is MediaGroup.SingleTrack -> trackRepository.getTrack(mediaGroup.trackId)
                    .map { listOf(it.track) }

                is MediaGroup.Artist -> trackRepository.getTracksForArtist(mediaGroup.artistId)
                is MediaGroup.Album -> trackRepository.getTracksForAlbum(mediaGroup.albumId)
                is MediaGroup.Genre -> trackRepository.getTracksForGenre(mediaGroup.genreId)
                is MediaGroup.Playlist -> trackRepository.getTracksForPlaylist(mediaGroup.playlistId)
            }.map { tracks -> tracks.map { it.toMediaItem() } }.first()
        }
    }

    override fun moveQueueItem(previousIndex: Int, newIndex: Int) {
        mediaPlaybackClient.moveQueueItem(previousIndex, newIndex)
    }

    override fun playQueueItem(index: Int) {
        mediaPlaybackClient.playQueueItem(index)
    }

    override fun addToQueue(tracks: List<Track>) {
        mediaPlaybackClient.addToQueue(tracks.map { it.toMediaItem() })
    }

    override fun getQueue(): Flow<List<TrackWithQueueId>> {
        return playbackInfoRepository.getSavedPlaybackInfo().map { it.queuedTracks }
    }

    override fun seekToTrackPosition(position: Long) {
        mediaPlaybackClient.seekToTrackPosition(position)
    }
}
