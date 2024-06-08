package com.sebastianvm.musicplayer.repository.playback

import androidx.media3.common.MediaItem
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaPlaybackClient
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.extensions.toMediaItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaybackManagerImpl(
    private val mediaPlaybackClient: MediaPlaybackClient,
    private val trackRepository: TrackRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : PlaybackManager {
    override fun getPlaybackState(): Flow<PlaybackState> = mediaPlaybackClient.playbackState

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

    private suspend fun playTracks(
        initialTrackIndex: Int = 0,
        tracksGetter: suspend () -> List<MediaItem>,
    ) {
        val mediaItems = withContext(ioDispatcher) { tracksGetter() }
        mediaPlaybackClient.playMediaItems(initialTrackIndex, mediaItems)
    }

    override suspend fun playMedia(mediaGroup: MediaGroup, initialTrackIndex: Int) {
        playTracks(initialTrackIndex) {
            trackRepository
                .getTracksForMedia(mediaGroup)
                .map { tracks -> tracks.map { it.toMediaItem() } }
                .first()
        }
    }

    override fun seekToTrackPosition(position: Long) {
        mediaPlaybackClient.seekToTrackPosition(position)
    }
}
