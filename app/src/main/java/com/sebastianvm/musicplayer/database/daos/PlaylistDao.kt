package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRefKeys
import com.sebastianvm.musicplayer.database.entities.PlaylistWithTracks
import com.sebastianvm.musicplayer.database.entities.TrackWithPlaylistPositionView
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

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

    @Query("SELECT playlistName FROM Playlist WHERE Playlist.id=:playlistId")
    fun getPlaylistName(playlistId: Long): Flow<String>

    @Transaction
    @Query("SELECT * FROM Playlist WHERE Playlist.id=:playlistId")
    fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks?>

    @Insert
    suspend fun createPlaylist(playlist: Playlist): Long

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Insert
    suspend fun addTrackToPlaylist(playlistTrackCrossRef: PlaylistTrackCrossRef)

    @Query("SELECT COUNT(*) FROM PlaylistTrackCrossRef WHERE PlaylistTrackCrossRef.playlistId=:playlistId")
    fun getPlaylistSize(playlistId: Long): Flow<Long>

    @Query("SELECT DISTINCT PlaylistTrackCrossRef.trackId FROM PlaylistTrackCrossRef WHERE PlaylistTrackCrossRef.playlistId=:playlistId")
    fun getTrackIdsInPlaylist(playlistId: Long): Flow<List<Long>>

    @Query(
        "SELECT * FROM TrackWithPlaylistPositionView WHERE TrackWithPlaylistPositionView.playlistId=:playlistId ORDER BY " +
            "CASE WHEN:sortOption='CUSTOM' AND :sortOrder='ASCENDING' THEN position END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='CUSTOM' AND :sortOrder='DESCENDING' THEN position END COLLATE LOCALIZED DESC, " +
            "CASE WHEN:sortOption='TRACK' AND :sortOrder='ASCENDING' THEN trackName END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='TRACK' AND :sortOrder='DESCENDING' THEN trackName END COLLATE LOCALIZED DESC, " +
            "CASE WHEN:sortOption='ARTIST' AND :sortOrder='ASCENDING' THEN artists END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='ARTIST' AND :sortOrder='DESCENDING' THEN artists END COLLATE LOCALIZED DESC, " +
            "CASE WHEN:sortOption='ALBUM' AND :sortOrder='ASCENDING' THEN albumName END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='ALBUM' AND :sortOrder='DESCENDING' THEN albumName END COLLATE LOCALIZED DESC"
    )
    fun getTracksInPlaylist(
        playlistId: Long,
        sortOption: SortOptions.PlaylistSortOptions,
        sortOrder: MediaSortOrder
    ): Flow<List<TrackWithPlaylistPositionView>>

    @Update
    suspend fun updatePlaylistItems(newItems: List<PlaylistTrackCrossRef>)

    @Delete(entity = PlaylistTrackCrossRef::class)
    suspend fun removePlaylistItem(playlistItemKeys: PlaylistTrackCrossRefKeys)

    @Transaction
    suspend fun removeItemFromPlaylist(playlistId: Long, position: Long) {
        val tracks = getTracksInPlaylist(
            playlistId,
            sortOption = SortOptions.PlaylistSortOptions.CUSTOM,
            sortOrder = MediaSortOrder.ASCENDING
        ).first()
        val lastItem = tracks.last()
            .let { PlaylistTrackCrossRefKeys(playlistId = it.playlistId, position = it.position) }
        val newTracks = withContext(Dispatchers.Default) {
            tracks.mapNotNull {
                when {
                    it.position < position -> {
                        PlaylistTrackCrossRef(
                            playlistId = it.playlistId,
                            trackId = it.id,
                            position = it.position
                        )
                    }

                    it.position > position -> {
                        PlaylistTrackCrossRef(
                            playlistId = it.playlistId,
                            trackId = it.id,
                            position = it.position - 1
                        )
                    }

                    else -> {
                        null
                    }
                }
            }
        }

        updatePlaylistItems(newTracks)
        removePlaylistItem(lastItem)
    }
}
