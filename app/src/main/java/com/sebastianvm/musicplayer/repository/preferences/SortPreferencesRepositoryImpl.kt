package com.sebastianvm.musicplayer.repository.preferences

import androidx.datastore.core.DataStore
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.util.sort.AlbumListSortOptions
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.PlaylistSortOptions
import com.sebastianvm.musicplayer.util.sort.SortPreferences
import com.sebastianvm.musicplayer.util.sort.TrackListSortOptions
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SortPreferencesRepositoryImpl @Inject constructor(
    private val sortPreferencesDataStore: DataStore<SortPreferences>,
) : SortPreferencesRepository {

    private suspend fun modifyAllTracksListSortPreferences(newPreferences: MediaSortPreferences<TrackListSortOptions>) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                allTracksListSortPreferences = newPreferences
            )
        }
    }


    private suspend fun modifyGenreTracksListSortPreferences(
        genreName: String,
        newPreferences: MediaSortPreferences<TrackListSortOptions>
    ) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                genreTracksListSortPreferences = oldPreferences.genreTracksListSortPreferences.mutate {
                    it[genreName] = newPreferences
                }
            )
        }
    }

    override suspend fun modifyTrackListSortPreferences(
        newPreferences: MediaSortPreferences<TrackListSortOptions>,
        tracksListType: TracksListType,
        tracksListName: String,
    ) {
        when (tracksListType) {
            TracksListType.ALL_TRACKS -> modifyAllTracksListSortPreferences(
                newPreferences
            )
            TracksListType.GENRE -> modifyGenreTracksListSortPreferences(
                tracksListName,
                newPreferences
            )
        }
    }

    private fun getAllTracksListSortPreferences(): Flow<MediaSortPreferences<TrackListSortOptions>> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.allTracksListSortPreferences
        }
    }

    private fun getGenreTracksListSortPreferences(genreName: String): Flow<MediaSortPreferences<TrackListSortOptions>> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.genreTracksListSortPreferences[genreName] ?: MediaSortPreferences(
                sortOption = TrackListSortOptions.TRACK,
                sortOrder = MediaSortOrder.ASCENDING
            )
        }
    }

    override fun getTracksListSortPreferences(
        tracksListType: TracksListType,
        tracksListName: String
    ): Flow<MediaSortPreferences<TrackListSortOptions>> {
        return when (tracksListType) {
            TracksListType.ALL_TRACKS -> getAllTracksListSortPreferences()
            TracksListType.GENRE -> getGenreTracksListSortPreferences(
                tracksListName
            )
        }
    }

    override suspend fun modifyAlbumsListSortPreferences(newPreferences: MediaSortPreferences<AlbumListSortOptions>) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                albumListSortPreferences = newPreferences
            )
        }
    }

    override fun getAlbumsListSortPreferences(): Flow<MediaSortPreferences<AlbumListSortOptions>> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.albumListSortPreferences
        }
    }

    override suspend fun modifyArtistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                artistListSortOrder = mediaSortOrder
            )
        }
    }


    override fun getArtistsListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.artistListSortOrder
        }
    }

    override suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                genreListSortOrder = mediaSortOrder
            )
        }

    }


    override fun getGenresListSortOrder(): Flow<MediaSortOrder> {
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
        playlistName: String,
        newPreferences: MediaSortPreferences<PlaylistSortOptions>
    ) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                playlistSortPreferences = oldPreferences.playlistSortPreferences.mutate {
                    it[playlistName] = newPreferences
                }
            )
        }
    }

    override fun getPlaylistSortPreferences(playlistName: String): Flow<MediaSortPreferences<PlaylistSortOptions>> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.playlistSortPreferences[playlistName] ?: MediaSortPreferences(
                sortOption = PlaylistSortOptions.CUSTOM,
                sortOrder = MediaSortOrder.ASCENDING
            )
        }
    }
}
