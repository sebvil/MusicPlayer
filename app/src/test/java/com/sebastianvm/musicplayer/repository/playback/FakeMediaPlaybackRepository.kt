package com.sebastianvm.musicplayer.repository.playback

import androidx.media3.common.MediaMetadata
import com.sebastianvm.musicplayer.database.entities.ArtistBuilder
import com.sebastianvm.musicplayer.database.entities.TrackBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import kotlinx.coroutines.flow.MutableStateFlow

class FakeMediaPlaybackRepository : MediaPlaybackRepository {

    override val nowPlaying: MutableStateFlow<MediaMetadata?> =
        MutableStateFlow(MediaMetadata.Builder().apply {
            setTitle(TrackBuilder.DEFAULT_TRACK_NAME)
            setArtist(ArtistBuilder.DEFAULT_ARTIST_NAME)
        }.build())
    override val playbackState: MutableStateFlow<PlaybackState> = MutableStateFlow(
        PlaybackState(
            isPlaying = false,
            currentPlayTimeMs = 10000,
        )
    )
    override val nowPlayingIndex: MutableStateFlow<Int> = MutableStateFlow(1)

    override fun connectToService() = Unit

    override fun disconnectFromService() = Unit

    override fun play() = Unit

    override fun pause() = Unit

    override fun next() = Unit

    override fun prev() = Unit

    override fun playFromId(mediaId: String, mediaGroup: MediaGroup) = Unit

    override fun moveQueueItem(previousIndex: Int, newIndex: Int) = Unit

    override fun playQueueItem(index: Int) = Unit
    override fun seekToTrackPosition(position: Long) = Unit

    override suspend fun addToQueue(mediaIds: List<String>): Int = 1
}