package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.fakegen.FakeCommandMethod
import com.sebastianvm.fakegen.FakeQueryMethod
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlinx.coroutines.flow.Flow

interface SortPreferencesRepository {

    @FakeCommandMethod
    suspend fun modifyTrackListSortPreferences(
        newPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>,
        trackList: TrackList
    )

    @FakeQueryMethod
    fun getTrackListSortPreferences(
        trackList: TrackList,
        trackListId: Long = 0
    ): Flow<MediaSortPreferences<SortOptions.TrackListSortOptions>>

    @FakeCommandMethod
    suspend fun modifyAlbumListSortPreferences(newPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>)

    @FakeQueryMethod
    fun getAlbumListSortPreferences(): Flow<MediaSortPreferences<SortOptions.AlbumListSortOptions>>

    @FakeCommandMethod
    suspend fun toggleArtistListSortOrder()

    @FakeQueryMethod
    fun getArtistListSortOrder(): Flow<MediaSortOrder>

    @FakeCommandMethod
    suspend fun toggleGenreListSortOrder()

    @FakeQueryMethod
    fun getGenreListSortOrder(): Flow<MediaSortOrder>

    @FakeCommandMethod
    suspend fun togglePlaylistListSortOder()

    @FakeQueryMethod
    fun getPlaylistsListSortOrder(): Flow<MediaSortOrder>

    @FakeCommandMethod
    suspend fun modifyPlaylistsSortPreferences(
        playlistId: Long,
        newPreferences: MediaSortPreferences<SortOptions.PlaylistSortOptions>
    )

    @FakeQueryMethod
    fun getPlaylistSortPreferences(playlistId: Long): Flow<MediaSortPreferences<SortOptions.PlaylistSortOptions>>
}
