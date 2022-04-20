package com.sebastianvm.musicplayer.repository.playback

import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaPlaybackClient
import com.sebastianvm.musicplayer.player.SavedPlaybackInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class MediaPlaybackRepositoryImpl @Inject constructor(
    private val mediaPlaybackClient: MediaPlaybackClient,
    private val playbackInfoDataSource: PlaybackInfoDataSource,
) : MediaPlaybackRepository {
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
