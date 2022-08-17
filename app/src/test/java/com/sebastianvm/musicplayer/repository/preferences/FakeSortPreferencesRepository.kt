package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.sort.SortPreferences
import com.sebastianvm.musicplayer.util.sort.not
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeSortPreferencesRepository(sortPreferences: SortPreferences = SortPreferences()) :
    SortPreferencesRepository {

    private val sortPreferencesState = MutableStateFlow(sortPreferences)

    override suspend fun modifyTrackListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>,
        trackListType: TrackListType,
        trackListId: Long
    ) {
        TODO("Not yet implemented")
    }

    override fun getTrackListSortPreferences(
        trackListType: TrackListType,
        trackListId: Long
    ): Flow<MediaSortPreferences<SortOptions.TrackListSortOptions>> {
        TODO("Not yet implemented")
    }

    override suspend fun modifyAlbumListSortPreferences(newPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>) {
        sortPreferencesState.value =
            sortPreferencesState.value.copy(albumListSortPreferences = newPreferences)
    }

    override fun getAlbumListSortPreferences(): Flow<MediaSortPreferences<SortOptions.AlbumListSortOptions>> {
        return sortPreferencesState.map { it.albumListSortPreferences }
    }

    override suspend fun toggleArtistListSortOrder() {
        sortPreferencesState.value =
            sortPreferencesState.value.copy(artistListSortOrder = !sortPreferencesState.value.artistListSortOrder)
    }

    override fun getArtistListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesState.map { it.artistListSortOrder }
    }

    override suspend fun toggleGenreListSortOrder() {
        sortPreferencesState.value =
            sortPreferencesState.value.copy(genreListSortOrder = !sortPreferencesState.value.genreListSortOrder)
    }

    override fun getGenreListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesState.map { it.genreListSortOrder }
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
