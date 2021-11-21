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
    suspend fun insertAllTracks(
        tracks: Set<Track>,
        artistTrackCrossRefs: Set<ArtistTrackCrossRef>,
        genreTrackCrossRefs: Set<GenreTrackCrossRef>,
        artists: Set<Artist>,
        genres: Set<Genre>,
        albums: Set<Album>,
        albumsForArtists: Set<AlbumsForArtist>,
        appearsOnForArtists: Set<AppearsOnForArtist>
    )

}