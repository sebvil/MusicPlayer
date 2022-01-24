package com.sebastianvm.musicplayer.repository.playback

import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.util.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow

interface MediaPlaybackRepository {

    val nowPlaying: MutableStateFlow<Track>
    val playbackState: MutableStateFlow<PlaybackState>


    fun play()
    fun pause()
    fun next()
    fun prev()
    fun playFromId(mediaId: String, mediaGroup: MediaGroup, sortOrder: SortOrder)
    fun moveQueueItem(previousIndex: Int, newIndex: Int)
    fun playQueueItem(index: Int)

}

data class PlaybackState(val isPlaying: Boolean, val currentPlayTimeMs: Long)
