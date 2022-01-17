package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.SortSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakePreferencesRepository : PreferencesRepository {

    private val albumSortSettings =
        MutableStateFlow(SortSettings(SortOption.ALBUM_NAME, SortOrder.ASCENDING))

    override suspend fun modifyTrackListSortOptions(
        sortSettings: SortSettings,
        genreName: String?
    ) = Unit

    override fun getTracksListSortOptions(genreName: String?): Flow<SortSettings> = flow {
        emit(
            SortSettings(sortOption = SortOption.TRACK_NAME, sortOrder = SortOrder.ASCENDING)
        )
    }

    override suspend fun modifyAlbumsListSortOptions(sortSettings: SortSettings) = albumSortSettings.emit(sortSettings)

    override fun getAlbumsListSortOptions(): Flow<SortSettings> = albumSortSettings

    override suspend fun modifyArtistsListSortOrder(sortOrder: SortOrder) = Unit

    override fun getArtistsListSortOrder(): Flow<SortOrder> = flow { emit(SortOrder.ASCENDING) }

    override suspend fun modifyGenresListSortOrder(sortOrder: SortOrder) = Unit

    override fun getGenresListSortOrder(): Flow<SortOrder> = flow { emit(SortOrder.ASCENDING) }
}
