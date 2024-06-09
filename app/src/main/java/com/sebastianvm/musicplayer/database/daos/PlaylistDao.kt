@file:Suppress("InjectDispatcher")

package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.sebastianvm.musicplayer.database.entities.PlaylistEntity
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRefKeys
import com.sebastianvm.musicplayer.database.entities.TrackWithPlaylistPositionView
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Dao
abstract class PlaylistDao {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    @Query(
        "SELECT * FROM PlaylistEntity ORDER BY " +
            "CASE WHEN :sortOrder='ASCENDING' THEN playlistName END COLLATE LOCALIZED ASC, " +
            "CASE WHEN :sortOrder='DESCENDING' THEN playlistName END COLLATE LOCALIZED DESC"
    )
    abstract fun getPlaylists(sortOrder: MediaSortOrder): Flow<List<PlaylistEntity>>

    @Query("SELECT playlistName FROM PlaylistEntity WHERE PlaylistEntity.id=:playlistId")
    abstract fun getPlaylistName(playlistId: Long): Flow<String>

    @Insert abstract suspend fun createPlaylist(playlist: PlaylistEntity): Long

    @Delete abstract suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Insert abstract suspend fun addTrackToPlaylist(playlistTrackCrossRef: PlaylistTrackCrossRef)

    @Query(
        "SELECT COUNT(*) FROM PlaylistTrackCrossRef WHERE PlaylistTrackCrossRef.playlistId=:playlistId"
    )
    abstract fun getPlaylistSize(playlistId: Long): Flow<Long>

    @Query(
        "SELECT DISTINCT PlaylistTrackCrossRef.trackId FROM PlaylistTrackCrossRef WHERE PlaylistTrackCrossRef.playlistId=:playlistId"
    )
    abstract fun getTrackIdsInPlaylist(playlistId: Long): Flow<List<Long>>

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
    abstract fun getTracksInPlaylist(
        playlistId: Long,
        sortOption: SortOptions.PlaylistSortOptions,
        sortOrder: MediaSortOrder,
    ): Flow<List<TrackWithPlaylistPositionView>>

    @Update abstract suspend fun updatePlaylistItems(newItems: List<PlaylistTrackCrossRef>)

    @Delete(entity = PlaylistTrackCrossRef::class)
    abstract suspend fun removePlaylistItem(playlistItemKeys: PlaylistTrackCrossRefKeys)

    @Transaction
    open suspend fun removeItemFromPlaylist(playlistId: Long, position: Long) {
        withContext(ioDispatcher) {
            val tracks =
                getTracksInPlaylist(
                        playlistId,
                        sortOption = SortOptions.PlaylistSortOptions.CUSTOM,
                        sortOrder = MediaSortOrder.ASCENDING,
                    )
                    .first()
            val lastItem =
                tracks.last().let {
                    PlaylistTrackCrossRefKeys(playlistId = it.playlistId, position = it.position)
                }
            val newTracks =
                withContext(defaultDispatcher) {
                    tracks.mapNotNull {
                        when {
                            it.position < position -> {
                                PlaylistTrackCrossRef(
                                    playlistId = it.playlistId,
                                    trackId = it.id,
                                    position = it.position,
                                )
                            }
                            it.position > position -> {
                                PlaylistTrackCrossRef(
                                    playlistId = it.playlistId,
                                    trackId = it.id,
                                    position = it.position - 1,
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
}
