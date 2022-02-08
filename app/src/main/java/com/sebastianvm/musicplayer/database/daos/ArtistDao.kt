package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import kotlinx.coroutines.flow.Flow


@Dao
interface ArtistDao {

    @Query("SELECT * from Artist")
    fun getArtists(): Flow<List<Artist>>

    @Transaction
    @Query("SELECT * from Artist WHERE Artist.artistName=:artistName")
    fun getArtist(artistName: String): Flow<ArtistWithAlbums>

    @Query("SELECT COUNT(*) FROM Artist")
    fun getArtistsCount(): Flow<Long>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT * FROM Artist " +
                "JOIN ArtistTrackCrossRef ON Artist.artistName=ArtistTrackCrossRef.artistName " +
                "WHERE ArtistTrackCrossRef.trackId=:trackId"
    )
    fun getArtistsForTrack(trackId: String): Flow<List<Artist>>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT * FROM Artist " +
                "JOIN AlbumsForArtist ON Artist.artistName=AlbumsForArtist.artistName " +
                "WHERE AlbumsForArtist.albumId=:albumId"
    )
    fun getArtistsForAlbum(albumId: String): Flow<List<Artist>>
}
