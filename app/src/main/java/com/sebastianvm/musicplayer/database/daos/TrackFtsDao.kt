package com.sebastianvm.musicplayer.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo

@Dao
interface TrackFtsDao {
    @Transaction
    @Query("SELECT * FROM Track " +
            "JOIN TrackFts ON Track.trackId == TrackFts.trackId " +
            "WHERE TrackFts.trackName MATCH :text" )
    fun tracksWithText(text: String): PagingSource<Int, FullTrackInfo>
}