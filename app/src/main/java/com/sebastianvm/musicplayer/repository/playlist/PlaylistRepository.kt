package com.sebastianvm.musicplayer.repository.playlist

import com.sebastianvm.model.BasicPlaylist
import com.sebastianvm.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<BasicPlaylist>>

    fun getPlaylistName(playlistId: Long): Flow<String>

    fun getPlaylist(playlistId: Long): Flow<Playlist>

    fun createPlaylist(playlistName: String): Flow<Long?>

    suspend fun deletePlaylist(playlistId: Long)

    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long)

    fun getTrackIdsInPlaylist(playlistId: Long): Flow<Set<Long>>

    suspend fun removeItemFromPlaylist(playlistId: Long, position: Long)
}
