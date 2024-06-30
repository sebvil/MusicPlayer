package com.sebastianvm.musicplayer.core.datastore.playinfo

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

internal class DefaultNowPlayingInfoDataSource(
    private val savedPlaybackInfoDataStore: DataStore<SavedPlaybackInfo>
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
