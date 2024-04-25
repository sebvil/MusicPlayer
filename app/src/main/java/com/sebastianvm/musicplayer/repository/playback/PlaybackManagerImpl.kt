package com.sebastianvm.musicplayer.repository.playback

import androidx.media3.common.MediaItem
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaPlaybackClient
import com.sebastianvm.musicplayer.player.PlaybackInfo
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.extensions.toMediaItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaybackManagerImpl(
    private val mediaPlaybackClient: MediaPlaybackClient,
    private val playbackInfoDataSource: PlaybackInfoDataSource,
    private val trackRepository: TrackRepository,
    private val ioDispatcher: CoroutineDispatcher
) : PlaybackManager {
    override fun getPlaybackState(): Flow<PlaybackState> =
        mediaPlaybackClient.playbackState

    override fun connectToService() {
        mediaPlaybackClient.initializeController()
    }

    override fun disconnectFromService() {
        mediaPlaybackClient.releaseController()
    }

    override fun togglePlay() {
        mediaPlaybackClient.togglePlay()
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

    override fun playMedia(mediaGroup: MediaGroup, initialTrackIndex: Int): Flow<PlaybackResult> {
        return playTracks(initialTrackIndex) {
            trackRepository.getTracksForMedia(mediaGroup).map { tracks ->
                tracks.map { it.toMediaItem() }
            }.first()
        }
    }

    override fun addToQueue(tracks: List<Track>) {
        mediaPlaybackClient.addToQueue(tracks.map { it.toMediaItem() })
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
