package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow


@Dao
interface ArtistDao {

    @Query("SELECT * from Artist ORDER BY " +
            "CASE WHEN :sortOrder='ASCENDING' THEN artistName END COLLATE LOCALIZED ASC, " +
            "CASE WHEN :sortOrder='DESCENDING' THEN artistName END COLLATE LOCALIZED DESC"
    )
    fun getArtists(sortOrder: MediaSortOrder): Flow<List<Artist>>

    @Transaction
    @Query("SELECT * from Artist WHERE Artist.artistName=:artistName")
    fun getArtist(artistName: String): Flow<ArtistWithAlbums>

    @Transaction
    @Query("SELECT * from Artist WHERE Artist.artistId=:artistId")
    fun getArtist(artistId: Long): Flow<ArtistWithAlbums>

    @Query("SELECT COUNT(*) FROM Artist")
    fun getArtistsCount(): Flow<Int>

    @Query(
        "SELECT Artist.* FROM Artist " +
                "JOIN ArtistTrackCrossRef ON Artist.artistId=ArtistTrackCrossRef.artistId " +
                "WHERE ArtistTrackCrossRef.trackId=:trackId"
    )
    fun getArtistsForTrack(trackId: String): Flow<List<Artist>>

    @Query(
        "SELECT Artist.* FROM Artist " +
                "JOIN AlbumsForArtist ON Artist.artistName=AlbumsForArtist.artistName " +
                "WHERE AlbumsForArtist.albumId=:albumId"
    )
    fun getArtistsForAlbum(albumId: Long): Flow<List<Artist>>
}
