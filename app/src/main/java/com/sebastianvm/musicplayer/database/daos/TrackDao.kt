package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
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

    @Transaction
    @Query("SELECT * FROM Track WHERE trackGid=:trackGid")
    fun getTrack(trackGid: String): Flow<FullTrackInfo>

    @Transaction
    @Query("""
        SELECT Track.* FROM Track 
        INNER JOIN ArtistTrackCrossRef ON Track.trackGid = ArtistTrackCrossRef.trackGid
        WHERE ArtistTrackCrossRef.artistGid=:artistId
    """)
    fun getTracksForArtist(artistId: String) : Flow<List<FullTrackInfo>>

    @Transaction
    @Query("""
        SELECT * FROM Track 
        WHERE Track.albumGid=:albumId
    """)
    fun getTracksForAlbum(albumId: String) : Flow<List<FullTrackInfo>>

    @Transaction
    @Query("""
        SELECT Track.* FROM Track 
        INNER JOIN GenreTrackCrossRef ON Track.trackGid = GenreTrackCrossRef.trackGid
        WHERE GenreTrackCrossRef.genreName=:genreName
    """)
    fun getTracksForGenre(genreName: String) : Flow<List<FullTrackInfo>>


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