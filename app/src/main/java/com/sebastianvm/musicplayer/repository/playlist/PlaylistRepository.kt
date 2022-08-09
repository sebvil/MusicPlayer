package com.sebastianvm.musicplayer.repository.playlist

import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.PlaylistWithTracks
import com.sebastianvm.musicplayer.database.entities.TrackWithPlaylistPositionView
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylistsCount(): Flow<Int>
    fun getPlaylists(sortOrder: MediaSortOrder): Flow<List<Playlist>>
    fun getPlaylist(playlistId: Long): Flow<Playlist?>
    suspend fun createPlaylist(playlistName: String)
    suspend fun deletePlaylist(playlistId: Long)
    fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks>
    suspend fun addTrackToPlaylist(playlistTrackCrossRef: PlaylistTrackCrossRef)
    fun getPlaylistSize(playlistId: Long): Flow<Long>
    fun getTrackIdsInPlaylist(playlistId: Long): Flow<Set<Long>>
    fun getTracksInPlaylist(playlistId: Long): Flow<List<TrackWithPlaylistPositionView>>


}
