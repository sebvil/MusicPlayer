package com.sebastianvm.musicplayer.repository.playlist

import com.sebastianvm.musicplayer.database.entities.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylistsCount(): Flow<Long>
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun createPlaylist(playlistName: String)
    suspend fun deletePlaylist(playlistName: String)
}
