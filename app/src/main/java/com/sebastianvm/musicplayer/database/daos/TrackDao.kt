package com.sebastianvm.musicplayer.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(
        track: Track,
        artistTrackCrossRefs: List<ArtistTrackCrossRef>,
        genreTrackCrossRef: List<GenreTrackCrossRef>
    )

    @Query("SELECT COUNT(*) FROM Track")
    suspend fun getTracksCount(): Long

    @Transaction
    @Query("SELECT * FROM Track")
    fun getAllTracks(): LiveData<List<FullTrackInfo>>

    @Transaction
    @Query("SELECT * FROM Track WHERE trackGid in (:trackGids)")
    fun getTracks(trackGids: List<String>): LiveData<List<FullTrackInfo>>

    @Transaction
    @Query(
        """
        SELECT Track.*
        FROM Track INNER JOIN GenreTrackCrossRef 
        ON Track.trackGid = GenreTrackCrossRef.trackGid 
        WHERE GenreTrackCrossRef.genreName = :genreName
        """
    )
    fun getTracksForGenre(genreName: String): LiveData<List<FullTrackInfo>>


}