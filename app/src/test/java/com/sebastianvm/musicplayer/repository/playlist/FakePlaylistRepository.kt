package com.sebastianvm.musicplayer.repository.playlist

import com.sebastianvm.musicplayer.model.BasicPlaylist
import com.sebastianvm.musicplayer.model.Playlist
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.extensions.mapValues
import com.sebastianvm.musicplayer.util.toBasicPlaylist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakePlaylistRepository : PlaylistRepository {

    val playlists: MutableStateFlow<List<Playlist>> = MutableStateFlow(FixtureProvider.playlists())

    var shouldPlaylistCreationFail: Boolean = false

    override fun getPlaylists(): Flow<List<BasicPlaylist>> {
        return playlists.mapValues { it.toBasicPlaylist() }
    }

    override fun getPlaylistName(playlistId: Long): Flow<String> {
        return playlists.map { playlists -> playlists.first { it.id == playlistId }.name }
    }

    override fun getPlaylist(playlistId: Long): Flow<Playlist> {
        return playlists.map { playlists -> playlists.first { it.id == playlistId } }
    }

    override fun createPlaylist(playlistName: String): Flow<Long?> {
        return if (shouldPlaylistCreationFail) {
            flowOf(null)
        } else {
            val newPlaylist = FixtureProvider.playlist(name = playlistName)
            playlists.update { playlists -> playlists + newPlaylist }
            flowOf(newPlaylist.id)
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlists.update { playlists ->
            playlists.toMutableList().apply { removeIf { it.id == playlistId } }
        }
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        playlists.update { playlists ->
            val index = playlists.indexOfFirst { it.id == playlistId }
            val playlist = playlists[index]
            val newTracks = playlist.tracks + FixtureProvider.track(id = trackId)
            playlists.toMutableList().apply { set(index, playlist.copy(tracks = newTracks)) }
        }
    }

    override fun getTrackIdsInPlaylist(playlistId: Long): Flow<Set<Long>> {
        return getPlaylist(playlistId).map { playlist -> playlist.tracks.map { it.id }.toSet() }
    }

    override suspend fun removeItemFromPlaylist(playlistId: Long, position: Long) {
        playlists.update { playlists ->
            val index = playlists.indexOfFirst { it.id == playlistId }
            val playlist = playlists[index]
            val newTracks = playlist.tracks.toMutableList().apply { removeAt(position.toInt()) }
            playlists.toMutableList().apply { set(index, playlist.copy(tracks = newTracks)) }
        }
    }
}
