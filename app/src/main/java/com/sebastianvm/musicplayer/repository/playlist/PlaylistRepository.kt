package com.sebastianvm.musicplayer.repository.playlist

import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.PlaylistWithTracks
import com.sebastianvm.musicplayer.database.entities.TrackWithPlaylistPositionView
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistName(playlistId: Long): Flow<String>
    fun createPlaylist(playlistName: String): Flow<Long?>
    suspend fun deletePlaylist(playlistId: Long)
    fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks>
    suspend fun addTrackToPlaylist(playlistTrackCrossRef: PlaylistTrackCrossRef)
    fun getPlaylistSize(playlistId: Long): Flow<Long>
    fun getTrackIdsInPlaylist(playlistId: Long): Flow<Set<Long>>
    fun getTracksInPlaylist(playlistId: Long): Flow<List<TrackWithPlaylistPositionView>>
    suspend fun removeItemFromPlaylist(playlistId: Long, position: Long)
}
