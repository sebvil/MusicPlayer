package com.sebastianvm.musicplayer.datastore

import androidx.datastore.core.DataStore
import com.sebastianvm.musicplayer.model.NowPlayingInfo
import kotlinx.coroutines.flow.Flow

class NowPlayingInfoDataSource(private val nowPlayingDataStore: DataStore<NowPlayingInfo>) {

    fun getNowPlayingInfo(): Flow<NowPlayingInfo> {
        return nowPlayingDataStore.data
    }

    suspend fun setNowPlayingInfo(nowPlayingInfo: NowPlayingInfo) {
        nowPlayingDataStore.updateData { nowPlayingInfo }
    }
}
