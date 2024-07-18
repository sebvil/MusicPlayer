package com.sebastianvm.musicplayer.core.playback.manager

import com.sebastianvm.musicplayer.core.data.track.TrackRepository
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.model.PlaybackState
import com.sebastianvm.musicplayer.core.model.Track
import com.sebastianvm.musicplayer.core.playback.extensions.toMediaItem
import com.sebastianvm.musicplayer.core.playback.player.MediaPlaybackClient
import com.sebastianvm.musicplayer.services.playback.PlaybackManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DefaultPlaybackManager(
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

    override fun moveQueueItem(from: Int, to: Int) {
        mediaPlaybackClient.moveQueueItem(from, to)
    }

    override suspend fun addToQueue(trackId: Long) {
        val track = trackRepository.getTrack(trackId).first()
        addToQueue(listOf(track))
    }

    private fun addToQueue(tracks: List<Track>) {
        mediaPlaybackClient.addToQueue(tracks.map { it.toMediaItem() })
    }

    override fun playQueueItem(index: Int) {
        mediaPlaybackClient.playQueueItem(index)
    }

    override fun removeItemsFromQueue(queuePositions: List<Int>) {
        mediaPlaybackClient.removeItemsFromQueue(queuePositions)
    }
}
