package com.sebastianvm.musicplayer.repository.playback

import androidx.media3.common.MediaMetadata
import com.sebastianvm.musicplayer.player.MediaGroup
import kotlinx.coroutines.flow.MutableStateFlow

class FakeMediaPlaybackRepository : MediaPlaybackRepository {

    override val nowPlaying: MutableStateFlow<MediaMetadata?> = MutableStateFlow(null)
    override val playbackState: MutableStateFlow<PlaybackState> = MutableStateFlow(
        PlaybackState(
            isPlaying = false,
            currentPlayTimeMs = 0,
            trackDurationMs = 0
        )
    )

    override fun connectToService() = Unit

    override fun disconnectFromService() = Unit

    override fun play() = Unit

    override fun pause() = Unit

    override fun next() = Unit

    override fun prev() = Unit

    override fun playFromId(mediaId: String, mediaGroup: MediaGroup) = Unit

    override fun moveQueueItem(previousIndex: Int, newIndex: Int) = Unit

    override fun playQueueItem(index: Int) = Unit
}