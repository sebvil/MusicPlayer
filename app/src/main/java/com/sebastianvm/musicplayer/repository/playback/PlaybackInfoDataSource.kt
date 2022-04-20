package com.sebastianvm.musicplayer.repository.playback

import com.sebastianvm.musicplayer.player.SavedPlaybackInfo
import kotlinx.coroutines.flow.Flow

interface PlaybackInfoDataSource {
    suspend fun modifySavedPlaybackInfo(transform: (savedPlaybackInfo: SavedPlaybackInfo) -> SavedPlaybackInfo)
    fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo>
}