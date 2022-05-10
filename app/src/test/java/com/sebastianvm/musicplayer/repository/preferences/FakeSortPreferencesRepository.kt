package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.sort.SortPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeSortPreferencesRepository(sortPreferences: SortPreferences = SortPreferences()) :
    SortPreferencesRepository {

    private val sortPreferencesState = MutableStateFlow(sortPreferences)

    override suspend fun modifyTrackListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>,
        tracksListType: TracksListType,
        tracksListId: Long
    ) {
        TODO("Not yet implemented")
    }

    override fun getTracksListSortPreferences(
        tracksListType: TracksListType,
        tracksListId: Long
    ): Flow<MediaSortPreferences<SortOptions.TrackListSortOptions>> {
        TODO("Not yet implemented")
    }

    override suspend fun modifyAlbumsListSortPreferences(newPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>) {
        sortPreferencesState.value =
            sortPreferencesState.value.copy(albumListSortPreferences = newPreferences)
    }

    override fun getAlbumsListSortPreferences(): Flow<MediaSortPreferences<SortOptions.AlbumListSortOptions>> {
        return sortPreferencesState.map { it.albumListSortPreferences }
    }

    override suspend fun modifyArtistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        sortPreferencesState.value =
            sortPreferencesState.value.copy(artistListSortOrder = mediaSortOrder)
    }

    override fun getArtistsListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesState.map { it.artistListSortOrder }
    }

    override suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder) {
        TODO("Not yet implemented")
    }

    override fun getGenresListSortOrder(): Flow<MediaSortOrder> {
        TODO("Not yet implemented")
    }

    override suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        TODO("Not yet implemented")
    }

    override fun getPlaylistsListSortOrder(): Flow<MediaSortOrder> {
        TODO("Not yet implemented")
    }

    override suspend fun modifyPlaylistsSortPreferences(
        playlistId: Long,
        newPreferences: MediaSortPreferences<SortOptions.PlaylistSortOptions>
    ) {
        TODO("Not yet implemented")
    }

    override fun getPlaylistSortPreferences(playlistId: Long): Flow<MediaSortPreferences<SortOptions.PlaylistSortOptions>> {
        TODO("Not yet implemented")
    }


}
