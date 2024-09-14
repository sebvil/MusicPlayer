package com.sebastianvm.musicplayer.core.datastore.playinfo

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Factory

@Factory
class DefaultNowPlayingInfoDataSource(
    private val savedPlaybackInfoDataStore: SavedPlaybackInfoDataStore
) : NowPlayingInfoDataSource {

    override fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo> {
        return savedPlaybackInfoDataStore.data
    }

    override suspend fun savePlaybackInfo(savedPlaybackInfo: SavedPlaybackInfo) {
        savedPlaybackInfoDataStore.updateData { savedPlaybackInfo }
    }
}

@Serializable
data class SavedPlaybackInfo(
    val nowPlayingPositionInQueue: Int = -1,
    val lastRecordedPosition: Long = -1L,
)
