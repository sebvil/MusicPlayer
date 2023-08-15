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

    @Query(
        "SELECT * from Artist ORDER BY " +
            "CASE WHEN :sortOrder='ASCENDING' THEN artistName END COLLATE LOCALIZED ASC, " +
            "CASE WHEN :sortOrder='DESCENDING' THEN artistName END COLLATE LOCALIZED DESC"
    )
    fun getArtists(sortOrder: MediaSortOrder): Flow<List<Artist>>

    @Transaction
    @Query("SELECT * from Artist WHERE Artist.id=:artistId")
    fun getArtist(artistId: Long): Flow<ArtistWithAlbums>

    @Query("SELECT COUNT(*) FROM Artist")
    fun getArtistsCount(): Flow<Int>

    @Query("SELECT * FROM ARTIST WHERE Artist.id IN (:artistsIds)")
    fun getArtists(artistsIds: List<Long>): Flow<List<Artist>>

    @Transaction
    @Query(
        "SELECT Artist.* FROM Artist " +
            "INNER JOIN ArtistTrackCrossRef " +
            "ON Artist.id = ArtistTrackCrossRef.artistId " +
            "WHERE ArtistTrackCrossRef.trackId=:trackId " +
            "ORDER BY artistName COLLATE LOCALIZED ASC"
    )
    fun getArtistsForTrack(trackId: Long): Flow<List<Artist>>

    @Transaction
    @Query(
        "SELECT Artist.* FROM Artist " +
            "INNER JOIN " +
            "(SELECT albumId, artistId FROM AlbumsForArtist UNION SELECT albumId, artistId FROM AppearsOnForArtist)" +
            "ON Artist.id = artistId " +
            "WHERE albumId=:albumId " +
            "ORDER BY artistName COLLATE LOCALIZED ASC"
    )
    fun getArtistsForAlbum(albumId: Long): Flow<List<Artist>>
}
