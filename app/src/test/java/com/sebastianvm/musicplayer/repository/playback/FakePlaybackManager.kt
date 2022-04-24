package com.sebastianvm.musicplayer.repository.playback

import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.PlaybackInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePlaybackManager(
    playbackState: PlaybackState = PlaybackState(
        mediaItemMetadata = null,
        isPlaying = false,
        currentPlayTimeMs = 0
    )
) : PlaybackManager {

    override val playbackState: MutableStateFlow<PlaybackState> = MutableStateFlow(playbackState)
    override val nowPlayingIndex: MutableStateFlow<Int> = MutableStateFlow(1)

    override fun connectToService() = Unit

    override fun disconnectFromService() = Unit

    override fun play() {
        playbackState.value = playbackState.value.copy(isPlaying = true)
    }

    override fun pause() {
        playbackState.value = playbackState.value.copy(isPlaying = false)
    }

    override fun next() = Unit

    override fun prev() = Unit

    override fun playFromId(mediaId: String, mediaGroup: MediaGroup) = Unit

    override fun moveQueueItem(previousIndex: Int, newIndex: Int) = Unit

    override fun playQueueItem(index: Int) = Unit
    override fun seekToTrackPosition(position: Long) = Unit

    override suspend fun addToQueue(mediaIds: List<String>) = 1
    override suspend fun modifySavedPlaybackInfo(transform: (playbackInfo: PlaybackInfo) -> PlaybackInfo) {
        TODO("Not yet implemented")
    }

    override fun getSavedPlaybackInfo(): Flow<PlaybackInfo> {
        TODO("Not yet implemented")
    }
}