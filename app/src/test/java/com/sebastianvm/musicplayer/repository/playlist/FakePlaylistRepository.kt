package com.sebastianvm.musicplayer.repository.playlist

import com.sebastianvm.musicplayer.model.Playlist
import com.sebastianvm.musicplayer.util.FixtureProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakePlaylistRepository : PlaylistRepository {

    val playlists: MutableStateFlow<List<Playlist>> = MutableStateFlow(FixtureProvider.playlists())

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlists
    }

    override fun getPlaylistName(playlistId: Long): Flow<String> {
        return playlists.map { playlists -> playlists.first { it.id == playlistId }.name }
    }

    override fun createPlaylist(playlistName: String): Flow<Long?> {
        val newPlaylist = FixtureProvider.playlist(name = playlistName)
        playlists.update { playlists -> playlists + newPlaylist }
        return flowOf(newPlaylist.id)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlists.update { playlists ->
            playlists.toMutableList().apply { removeIf { it.id == playlistId } }
        }
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        TODO("Not yet implemented")
    }

    override fun getTrackIdsInPlaylist(playlistId: Long): Flow<Set<Long>> {
        TODO("Not yet implemented")
    }

    override suspend fun removeItemFromPlaylist(playlistId: Long, position: Long) {
        TODO("Not yet implemented")
    }
}
