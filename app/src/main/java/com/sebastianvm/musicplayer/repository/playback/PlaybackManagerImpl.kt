package com.sebastianvm.musicplayer.repository.playback

import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaPlaybackClient
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.extensions.toMediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PlaybackManagerImpl(
    private val mediaPlaybackClient: MediaPlaybackClient,
    private val trackRepository: TrackRepository,
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

    override suspend fun playMedia(mediaGroup: MediaGroup, initialTrackIndex: Int) {
        val mediaItems =
            trackRepository
                .getTracksForMedia(mediaGroup)
                .map { tracks -> tracks.map { it.toMediaItem() } }
                .first()
        mediaPlaybackClient.playMediaItems(initialTrackIndex, mediaItems)
    }

    override fun seekToTrackPosition(position: Long) {
        mediaPlaybackClient.seekToTrackPosition(position)
    }
}
