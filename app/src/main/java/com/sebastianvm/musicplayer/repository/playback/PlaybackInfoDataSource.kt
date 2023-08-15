package com.sebastianvm.musicplayer.repository.playback

import com.sebastianvm.musicplayer.player.PlaybackInfo
import kotlinx.coroutines.flow.Flow

interface PlaybackInfoDataSource {
    suspend fun modifySavedPlaybackInfo(newPlaybackInfo: PlaybackInfo)
    fun getSavedPlaybackInfo(): Flow<PlaybackInfo>
}
