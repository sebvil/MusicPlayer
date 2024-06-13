package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.sort.not
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

typealias TrackListSortPreferencesMap =
    Map<Long, MediaSortPreferences<SortOptions.TrackListSortOptions>>

typealias PlaylistPreferencesMap = Map<Long, MediaSortPreferences<SortOptions.PlaylistSortOptions>>

class FakeSortPreferencesRepository : SortPreferencesRepository {

    val albumListSortPreferences:
        MutableStateFlow<MediaSortPreferences<SortOptions.AlbumListSortOptions>> =
        MutableStateFlow(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.ALBUM,
                sortOrder = MediaSortOrder.ASCENDING,
            )
        )

    val artistListSortOrder: MutableStateFlow<MediaSortOrder> =
        MutableStateFlow(MediaSortOrder.ASCENDING)

    val genreListSortOrder: MutableStateFlow<MediaSortOrder> =
        MutableStateFlow(MediaSortOrder.ASCENDING)

    val allTracksSortPreferences:
        MutableStateFlow<MediaSortPreferences<SortOptions.TrackListSortOptions>> =
        MutableStateFlow(
            MediaSortPreferences(SortOptions.TrackListSortOptions.TRACK, MediaSortOrder.ASCENDING)
        )

    val genreTracksSortPreferences: MutableStateFlow<TrackListSortPreferencesMap> =
        MutableStateFlow(emptyMap())

    val playlistTracksSortPreferences: MutableStateFlow<PlaylistPreferencesMap> =
        MutableStateFlow(emptyMap())

    val playlistListSortOrder: MutableStateFlow<MediaSortOrder> =
        MutableStateFlow(MediaSortOrder.ASCENDING)

    override suspend fun modifyTrackListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>,
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
    ): Flow<MediaSortPreferences<SortOptions.TrackListSortOptions>> {
        return when (trackList) {
            is MediaGroup.AllTracks -> allTracksSortPreferences
            is MediaGroup.Genre ->
                genreTracksSortPreferences.map {
                    it[trackList.genreId]
                        ?: MediaSortPreferences(
                            SortOptions.TrackListSortOptions.TRACK,
                            MediaSortOrder.ASCENDING,
                        )
                }
            else -> error("Cannot sort $trackList")
        }
    }

    override suspend fun modifyAlbumListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>
    ) {
        albumListSortPreferences.value = newPreferences
    }

    override fun getAlbumListSortPreferences():
        Flow<MediaSortPreferences<SortOptions.AlbumListSortOptions>> {
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
        newPreferences: MediaSortPreferences<SortOptions.PlaylistSortOptions>,
    ) {
        playlistTracksSortPreferences.update { it + (playlistId to newPreferences) }
    }

    override fun getPlaylistSortPreferences(
        playlistId: Long
    ): Flow<MediaSortPreferences<SortOptions.PlaylistSortOptions>> {
        return playlistTracksSortPreferences.map {
            it[playlistId]
                ?: MediaSortPreferences(
                    SortOptions.PlaylistSortOptions.TRACK,
                    MediaSortOrder.ASCENDING,
                )
        }
    }
}
