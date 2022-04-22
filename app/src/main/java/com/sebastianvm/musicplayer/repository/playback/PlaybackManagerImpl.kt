package com.sebastianvm.musicplayer.repository.playback

import androidx.media3.common.MediaItem
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaPlaybackClient
import com.sebastianvm.musicplayer.player.SavedPlaybackInfo
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import com.sebastianvm.musicplayer.util.extensions.toMediaItem
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaybackManagerImpl @Inject constructor(
    private val mediaPlaybackClient: MediaPlaybackClient,
    private val playbackInfoDataSource: PlaybackInfoDataSource,
    private val trackRepository: TrackRepository,
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

    private suspend fun playTracks(
        startingTrackId: Long,
        tracksGetter: suspend () -> List<MediaItem>
    ) {
        val mediaItems = withContext(ioDispatcher) {
            tracksGetter()
        }
        mediaPlaybackClient.playMediaItems(startingTrackId, mediaItems)
    }

    override suspend fun playAllTracks(
        startingTrackId: Long,
        mediaSortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>
    ) {
        playTracks(startingTrackId) {
            trackRepository.getAllTracks(mediaSortPreferences).first().map { it.toMediaItem() }
        }
    }

    override suspend fun playGenre(
        genreName: String,
        startingTrackId: Long,
        mediaSortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>
    ) {
        playTracks(startingTrackId) {
            trackRepository.getTracksForGenre(genreName, mediaSortPreferences).first()
                .map { it.toMediaItem() }
        }
    }

    override fun playFromId(mediaId: String, mediaGroup: MediaGroup) {
        mediaPlaybackClient.playFromId(mediaId, mediaGroup)
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
