package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.core.datastore.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.core.datastore.sort.SortPreferencesDataSource
import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.core.model.not
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SortPreferencesRepositoryImpl(
    private val sortPreferencesDataStore: SortPreferencesDataSource
) : SortPreferencesRepository {

    private suspend fun modifyAllTrackListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.TrackListSortOption>
    ) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(allTrackListSortPreferences = newPreferences)
        }
    }

    private suspend fun modifyGenreTrackListSortPreferences(
        genreId: Long,
        newPreferences: MediaSortPreferences<SortOptions.TrackListSortOption>,
    ) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                genreTrackListSortPreferences =
                    oldPreferences.genreTrackListSortPreferences.mutate {
                        it[genreId] = newPreferences
                    })
        }
    }

    override suspend fun modifyTrackListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.TrackListSortOption>,
        trackList: TrackList,
    ) {
        when (trackList) {
            is MediaGroup.AllTracks -> {
                modifyAllTrackListSortPreferences(newPreferences)
            }
            is MediaGroup.Genre -> {
                modifyGenreTrackListSortPreferences(trackList.genreId, newPreferences)
            }
            else ->
                throw IllegalArgumentException(
                    "Invalid trackListType for modifyTrackListSortPreferences")
        }
    }

    private fun getAllTrackListSortPreferences():
        Flow<MediaSortPreferences<SortOptions.TrackListSortOption>> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.allTrackListSortPreferences
        }
    }

    private fun getGenreTrackListSortPreferences(
        genreId: Long
    ): Flow<MediaSortPreferences<SortOptions.TrackListSortOption>> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.genreTrackListSortPreferences[genreId]
                ?: MediaSortPreferences(
                    sortOption = SortOptions.Track,
                    sortOrder = MediaSortOrder.ASCENDING,
                )
        }
    }

    private fun getPlaylistTrackListSortPreferences(
        genreId: Long
    ): Flow<MediaSortPreferences<SortOptions.TrackListSortOption>> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.playlistTrackListSortPreferences[genreId]
                ?: MediaSortPreferences(
                    sortOption = SortOptions.Track,
                    sortOrder = MediaSortOrder.ASCENDING,
                )
        }
    }

    override fun getTrackListSortPreferences(
        trackList: TrackList
    ): Flow<MediaSortPreferences<SortOptions.TrackListSortOption>> {
        return when (trackList) {
            is MediaGroup.AllTracks -> getAllTrackListSortPreferences()
            is MediaGroup.Genre -> getGenreTrackListSortPreferences(trackList.genreId)
            is MediaGroup.Playlist -> getPlaylistTrackListSortPreferences(trackList.playlistId)
            else ->
                throw IllegalArgumentException(
                    "Invalid trackListType $trackList for getTrackListSortPreferences")
        }
    }

    override suspend fun modifyAlbumListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.AlbumListSortOption>
    ) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(albumListSortPreferences = newPreferences)
        }
    }

    override fun getAlbumListSortPreferences():
        Flow<MediaSortPreferences<SortOptions.AlbumListSortOption>> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.albumListSortPreferences
        }
    }

    override suspend fun toggleArtistListSortOrder() {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(artistListSortOrder = !oldPreferences.artistListSortOrder)
        }
    }

    override fun getArtistListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesDataStore.data.map { preferences -> preferences.artistListSortOrder }
    }

    override suspend fun toggleGenreListSortOrder() {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(genreListSortOrder = !oldPreferences.genreListSortOrder)
        }
    }

    override fun getGenreListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesDataStore.data.map { preferences -> preferences.genreListSortOrder }
    }

    override suspend fun togglePlaylistListSortOder() {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(playlistListSortOrder = !oldPreferences.playlistListSortOrder)
        }
    }

    override fun getPlaylistsListSortOrder(): Flow<MediaSortOrder> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.playlistListSortOrder
        }
    }

    override suspend fun modifyPlaylistsSortPreferences(
        playlistId: Long,
        newPreferences: MediaSortPreferences<SortOptions.PlaylistSortOption>,
    ) {
        sortPreferencesDataStore.updateData { oldPreferences ->
            oldPreferences.copy(
                playlistSortPreferences =
                    oldPreferences.playlistSortPreferences.mutate {
                        it[playlistId] = newPreferences
                    })
        }
    }

    override fun getPlaylistSortPreferences(
        playlistId: Long
    ): Flow<MediaSortPreferences<SortOptions.PlaylistSortOption>> {
        return sortPreferencesDataStore.data.map { preferences ->
            preferences.playlistSortPreferences[playlistId]
                ?: MediaSortPreferences(
                    sortOption = SortOptions.Custom,
                    sortOrder = MediaSortOrder.ASCENDING,
                )
        }
    }
}
