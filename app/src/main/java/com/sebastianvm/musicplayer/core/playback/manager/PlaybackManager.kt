package com.sebastianvm.musicplayer.core.playback

import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.model.PlaybackState
import kotlinx.coroutines.flow.Flow

interface PlaybackManager {

    fun getPlaybackState(): Flow<PlaybackState>

    fun connectToService()

    fun disconnectFromService()

    fun togglePlay()

    fun next()

    fun prev()

    suspend fun playMedia(mediaGroup: MediaGroup, initialTrackIndex: Int = 0)

    fun seekToTrackPosition(position: Long)

    fun moveQueueItem(from: Int, to: Int)

    suspend fun addToQueue(trackId: Long)

    fun playQueueItem(index: Int)

    fun removeItemsFromQueue(queuePositions: List<Int>)
}
