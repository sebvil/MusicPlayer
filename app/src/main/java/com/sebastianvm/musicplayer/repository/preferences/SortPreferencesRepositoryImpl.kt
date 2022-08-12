package com.sebastianvm.musicplayer.repository.preferences

import androidx.datastore.core.DataStore
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.sort.SortPreferences
import com.sebastianvm.musicplayer.util.sort.not
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SortPreferencesRepositoryImpl @Inject constructor(
    private val sortPreferencesDataStore: DataStore<SortPreferences>,
) : SortPreferencesRepository {

    private suspend fun modifyAllTrackListSortPreferences(newPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                allTrackListSortPreferences = newPreferences
            )
        }
    }


    private suspend fun modifyGenreTrackListSortPreferences(
        genreId: Long,
        newPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>
    ) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                genreTrackListSortPreferences = oldPreferences.genreTrackListSortPreferences.mutate {
                    it[genreId] = newPreferences
                }
            )
        }
    }

    override suspend fun modifyTrackListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>,
        trackListType: TrackListType,
        trackListId: Long,
    ) {
        when (trackListType) {
            TrackListType.ALL_TRACKS -> modifyAllTrackListSortPreferences(
                newPreferences
            )
            TrackListType.GENRE -> modifyGenreTrackListSortPreferences(
                trackListId,
                newPreferences
            )
        }
    }

    private fun getAllTrackListSortPreferences(): Flow<MediaSortPreferences<SortOptions.TrackListSortOptions>> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.allTrackListSortPreferences
        }
    }

    private fun getGenreTrackListSortPreferences(genreId: Long): Flow<MediaSortPreferences<SortOptions.TrackListSortOptions>> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.genreTrackListSortPreferences[genreId] ?: MediaSortPreferences(
                sortOption = SortOptions.TrackListSortOptions.TRACK,
                sortOrder = MediaSortOrder.ASCENDING
            )
        }
    }

    override fun getTrackListSortPreferences(
        trackListType: TrackListType,
        trackListId: Long
    ): Flow<MediaSortPreferences<SortOptions.TrackListSortOptions>> {
        return when (trackListType) {
            TrackListType.ALL_TRACKS -> getAllTrackListSortPreferences()
            TrackListType.GENRE -> getGenreTrackListSortPreferences(trackListId)
        }
    }

    override suspend fun modifyAlbumListSortPreferences(newPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                albumListSortPreferences = newPreferences
            )
        }
    }

    override fun getAlbumListSortPreferences(): Flow<MediaSortPreferences<SortOptions.AlbumListSortOptions>> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.albumListSortPreferences
        }
    }

    override suspend fun toggleArtistListSortOrder() {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                artistListSortOrder = !oldPreferences.artistListSortOrder
            )
        }
    }


    override fun getArtistListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.artistListSortOrder
        }
    }

    override suspend fun modifyGenreListSortOrder(mediaSortOrder: MediaSortOrder) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                genreListSortOrder = mediaSortOrder
            )
        }

    }


    override fun getGenreListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.genreListSortOrder
        }
    }

    override suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                playlistListSortOrder = mediaSortOrder
            )
        }
    }


    override fun getPlaylistsListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.playlistListSortOrder
        }
    }

    override suspend fun modifyPlaylistsSortPreferences(
        playlistId: Long,
        newPreferences: MediaSortPreferences<SortOptions.PlaylistSortOptions>
    ) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                playlistSortPreferences = oldPreferences.playlistSortPreferences.mutate {
                    it[playlistId] = newPreferences
                }
            )
        }
    }

    override fun getPlaylistSortPreferences(playlistId: Long): Flow<MediaSortPreferences<SortOptions.PlaylistSortOptions>> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.playlistSortPreferences[playlistId] ?: MediaSortPreferences(
                sortOption = SortOptions.PlaylistSortOptions.CUSTOM,
                sortOrder = MediaSortOrder.ASCENDING
            )
        }
    }
}
