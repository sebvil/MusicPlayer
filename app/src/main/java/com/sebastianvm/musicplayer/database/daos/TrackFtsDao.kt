package com.sebastianvm.musicplayer.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackFtsDao {
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT DISTINCT Track.* FROM Track " +
                "JOIN TrackFts ON Track.id == TrackFts.trackId " +
                "WHERE TrackFts MATCH :text ORDER BY Track.trackName"
    )
    fun tracksWithTextPaged(text: String): PagingSource<Int, FullTrackInfo>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT DISTINCT Track.* FROM Track " +
                "JOIN TrackFts ON Track.id == TrackFts.trackId " +
                "WHERE TrackFts MATCH :text ORDER BY Track.trackName"
    )
    fun tracksWithText(text: String): Flow<List<Track>>
}