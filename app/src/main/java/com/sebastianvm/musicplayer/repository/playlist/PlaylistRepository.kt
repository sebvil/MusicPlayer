package com.sebastianvm.musicplayer.repository.playlist

import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylistsCount(): Flow<Int>
    fun getPlaylists(sortOrder: MediaSortOrder): Flow<List<Playlist>>
    suspend fun createPlaylist(playlistName: String)
    suspend fun deletePlaylist(playlistId: Long)
}
