package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.PlaylistWithTracks
import com.sebastianvm.musicplayer.database.entities.TrackWithPlaylistPositionView
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT COUNT(*) FROM Playlist")
    fun getPlaylistsCount(): Flow<Int>

    @Transaction
    @Query("SELECT * FROM Playlist")
    fun getPlaylistsWithTracks(): Flow<List<PlaylistWithTracks>>

    @Transaction
    @Query("SELECT * FROM Playlist WHERE Playlist.playlistName=:playlistName")
    fun getPlaylistWithTracks(playlistName: String): Flow<PlaylistWithTracks>

    @Query(
        "SELECT * FROM Playlist ORDER BY " +
                "CASE WHEN :sortOrder='ASCENDING' THEN playlistName END COLLATE LOCALIZED ASC, " +
                "CASE WHEN :sortOrder='DESCENDING' THEN playlistName END COLLATE LOCALIZED DESC"
    )
    fun getPlaylists(sortOrder: MediaSortOrder): Flow<List<Playlist>>

    @Query("SELECT * FROM Playlist WHERE Playlist.id=:playlistId")
    fun getPlaylist(playlistId: Long): Flow<Playlist?>

    @Transaction
    @Query("SELECT * FROM Playlist WHERE Playlist.id=:playlistId")
    fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks?>

    @Insert
    suspend fun createPlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Insert
    suspend fun addTrackToPlaylist(playlistTrackCrossRef: PlaylistTrackCrossRef)

    @Query("SELECT COUNT(*) FROM PlaylistTrackCrossRef WHERE PlaylistTrackCrossRef.playlistId=:playlistId")
    fun getPlaylistSize(playlistId: Long): Flow<Long>

    @Query("SELECT DISTINCT PlaylistTrackCrossRef.trackId FROM PlaylistTrackCrossRef WHERE PlaylistTrackCrossRef.playlistId=:playlistId")
    fun getTrackIdsInPlaylist(playlistId: Long): Flow<List<Long>>

    @Query("SELECT * FROM TrackWithPlaylistPositionView WHERE TrackWithPlaylistPositionView.playlistId=:playlistId")
    fun getTracksInPlaylist(playlistId: Long): Flow<List<TrackWithPlaylistPositionView>>

}
