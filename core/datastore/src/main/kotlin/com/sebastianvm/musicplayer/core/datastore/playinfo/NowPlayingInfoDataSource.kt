package com.sebastianvm.musicplayer.core.datastore.playinfo

import kotlinx.coroutines.flow.Flow

interface NowPlayingInfoDataSource {
    fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo>

    suspend fun savePlaybackInfo(savedPlaybackInfo: SavedPlaybackInfo)
}
