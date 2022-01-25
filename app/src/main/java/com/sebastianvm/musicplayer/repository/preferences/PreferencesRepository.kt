package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.player.CurrentPlaybackInfo
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.SortSettings
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun modifyTrackListSortOptions(sortSettings: SortSettings, genreName: String?)

    fun getTracksListSortOptions(genreName: String?): Flow<SortSettings>

    suspend fun modifyAlbumsListSortOptions(sortSettings: SortSettings)

    fun getAlbumsListSortOptions(): Flow<SortSettings>

    suspend fun modifyArtistsListSortOrder(sortOrder: SortOrder)

    fun getArtistsListSortOrder(): Flow<SortOrder>

    suspend fun modifyGenresListSortOrder(sortOrder: SortOrder)

    fun getGenresListSortOrder(): Flow<SortOrder>

    suspend fun modifyCurrentPlaybackInfo(playbackInfo: CurrentPlaybackInfo)

    fun getCurrentPlaybackInfo(): Flow<CurrentPlaybackInfo>
}
