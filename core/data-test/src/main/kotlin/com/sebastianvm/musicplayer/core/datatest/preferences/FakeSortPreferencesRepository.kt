package com.sebastianvm.musicplayer.core.datatest.preferences

import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.datastore.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.core.model.TrackList
import com.sebastianvm.musicplayer.core.model.not
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

typealias TrackListSortPreferencesMap =
    Map<Long, MediaSortPreferences<SortOptions.TrackListSortOption>>

typealias PlaylistPreferencesMap = Map<Long, MediaSortPreferences<SortOptions.PlaylistSortOption>>

class FakeSortPreferencesRepository : SortPreferencesRepository {

    val albumListSortPreferences:
        MutableStateFlow<MediaSortPreferences<SortOptions.AlbumListSortOption>> =
        MutableStateFlow(
            MediaSortPreferences(
                sortOption = SortOptions.Album,
                sortOrder = MediaSortOrder.ASCENDING,
            ))

    val artistListSortOrder: MutableStateFlow<MediaSortOrder> =
        MutableStateFlow(MediaSortOrder.ASCENDING)

    val genreListSortOrder: MutableStateFlow<MediaSortOrder> =
        MutableStateFlow(MediaSortOrder.ASCENDING)

    val allTracksSortPreferences:
        MutableStateFlow<MediaSortPreferences<SortOptions.TrackListSortOption>> =
        MutableStateFlow(MediaSortPreferences(SortOptions.Track, MediaSortOrder.ASCENDING))

    val genreTracksSortPreferences: MutableStateFlow<TrackListSortPreferencesMap> =
        MutableStateFlow(emptyMap())

    val playlistTracksSortPreferences: MutableStateFlow<PlaylistPreferencesMap> =
        MutableStateFlow(emptyMap())

    val playlistListSortOrder: MutableStateFlow<MediaSortOrder> =
        MutableStateFlow(MediaSortOrder.ASCENDING)

    override suspend fun modifyTrackListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.TrackListSortOption>,
        trackList: TrackList,
    ) {
        when (trackList) {
            is MediaGroup.AllTracks -> allTracksSortPreferences.value = newPreferences
            is MediaGroup.Genre ->
                genreTracksSortPreferences.update { it + (trackList.genreId to newPreferences) }
            else -> error("Cannot sort $trackList")
        }
    }

    override fun getTrackListSortPreferences(
        trackList: TrackList
    ): Flow<MediaSortPreferences<SortOptions.TrackListSortOption>> {
        return when (trackList) {
            is MediaGroup.AllTracks -> allTracksSortPreferences
            is MediaGroup.Genre ->
                genreTracksSortPreferences.map {
                    it[trackList.genreId]
                        ?: MediaSortPreferences(SortOptions.Track, MediaSortOrder.ASCENDING)
                }
            else -> error("Cannot sort $trackList")
        }
    }

    override suspend fun modifyAlbumListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.AlbumListSortOption>
    ) {
        albumListSortPreferences.value = newPreferences
    }

    override fun getAlbumListSortPreferences():
        Flow<MediaSortPreferences<SortOptions.AlbumListSortOption>> {
        return albumListSortPreferences
    }

    override suspend fun toggleArtistListSortOrder() {
        artistListSortOrder.update { !it }
    }

    override fun getArtistListSortOrder(): Flow<MediaSortOrder> {
        return artistListSortOrder
    }

    override suspend fun toggleGenreListSortOrder() {
        genreListSortOrder.update { !it }
    }

    override fun getGenreListSortOrder(): Flow<MediaSortOrder> {
        return genreListSortOrder
    }

    override suspend fun togglePlaylistListSortOder() {
        playlistListSortOrder.update { !it }
    }

    override fun getPlaylistsListSortOrder(): Flow<MediaSortOrder> {
        return playlistListSortOrder
    }

    override suspend fun modifyPlaylistsSortPreferences(
        playlistId: Long,
        newPreferences: MediaSortPreferences<SortOptions.PlaylistSortOption>,
    ) {
        playlistTracksSortPreferences.update { it + (playlistId to newPreferences) }
    }

    override fun getPlaylistSortPreferences(
        playlistId: Long
    ): Flow<MediaSortPreferences<SortOptions.PlaylistSortOption>> {
        return playlistTracksSortPreferences.map {
            it[playlistId] ?: MediaSortPreferences(SortOptions.Track, MediaSortOrder.ASCENDING)
        }
    }
}
