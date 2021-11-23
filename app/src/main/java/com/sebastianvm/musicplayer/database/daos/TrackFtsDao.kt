package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.sebastianvm.musicplayer.database.entities.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackFtsDao {
    @Query("SELECT * FROM Track JOIN TrackFts ON Track.trackGid == TrackFts.trackGid WHERE TrackFts.trackName MATCH :text" )
    fun tracksWithText(text: String): Flow<List<Track>>
}