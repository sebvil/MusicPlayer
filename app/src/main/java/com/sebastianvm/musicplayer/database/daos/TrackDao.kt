package com.sebastianvm.musicplayer.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sebastianvm.musicplayer.database.entities.*

@Dao
interface TrackDao {

    @Query("SELECT COUNT(*) FROM Track")
    fun getTracksCount(): LiveData<Long>

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


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun newInsertTrack(
        track: Track,
        artistTrackCrossRefs: List<ArtistTrackCrossRef>,
        genreTrackCrossRef: List<GenreTrackCrossRef>,
        artists: List<Artist>,
        genres: List<Genre>,
        album: Album,
        albumForArtists: List<AlbumsForArtist>,
        appearsOnForArtist: List<AppearsOnForArtist>
    )

}