package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.BasicTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackFtsDao {

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT DISTINCT TrackEntity.* FROM TrackEntity " +
            "JOIN TrackFts ON TrackEntity.id == TrackFts.trackId " +
            "WHERE TrackFts MATCH :text ORDER BY TrackEntity.trackName"
    )
    fun tracksWithText(text: String): Flow<List<BasicTrackEntity>>
}
