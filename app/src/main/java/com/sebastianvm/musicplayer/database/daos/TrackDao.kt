package com.sebastianvm.musicplayer.database.daos

import androidx.room.*
import com.sebastianvm.musicplayer.database.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT COUNT(*) FROM Track")
    fun getTracksCount(): Flow<Long>

    @Transaction
    @Query("SELECT * FROM Track")
    fun getAllTracks(): Flow<List<FullTrackInfo>>

    @Transaction
    @Query("SELECT * FROM Track WHERE trackGid in (:trackGids)")
    fun getTracks(trackGids: List<String>): Flow<List<FullTrackInfo>>

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