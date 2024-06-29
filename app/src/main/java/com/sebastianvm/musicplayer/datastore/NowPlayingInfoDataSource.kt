package com.sebastianvm.musicplayer.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

class NowPlayingInfoDataSource(
    private val savedPlaybackInfoDataStore: DataStore<SavedPlaybackInfo>
) {

    fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo> {
        return savedPlaybackInfoDataStore.data
    }

    suspend fun savePlaybackInfo(savedPlaybackInfo: SavedPlaybackInfo) {
        savedPlaybackInfoDataStore.updateData { savedPlaybackInfo }
    }
}

@Serializable
data class SavedPlaybackInfo(
    val nowPlayingPositionInQueue: Int = -1,
    val lastRecordedPosition: Long = -1L,
)
