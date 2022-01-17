package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.SortSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePreferencesRepository : PreferencesRepository {
    override suspend fun modifyTrackListSortOptions(
        sortSettings: SortSettings,
        genreName: String?
    ) = Unit

    override fun getTracksListSortOptions(genreName: String?): Flow<SortSettings> = flow {
        emit(
            SortSettings(sortOption = SortOption.TRACK_NAME, sortOrder = SortOrder.ASCENDING)
        )
    }

    override suspend fun modifyAlbumsListSortOptions(sortSettings: SortSettings) = Unit

    override fun getAlbumsListSortOptions(): Flow<SortSettings> = flow {
        emit(
            SortSettings(
                sortOption = SortOption.ALBUM_NAME,
                sortOrder = SortOrder.ASCENDING
            )
        )
    }

    override suspend fun modifyArtistsListSortOrder(sortOrder: SortOrder) = Unit

    override fun getArtistsListSortOrder(): Flow<SortOrder> = flow { emit(SortOrder.ASCENDING) }

    override suspend fun modifyGenresListSortOrder(sortOrder: SortOrder) = Unit

    override fun getGenresListSortOrder(): Flow<SortOrder> = flow { emit(SortOrder.ASCENDING) }
}
