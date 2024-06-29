package com.sebastianvm.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.database.entities.ArtistEntity
import com.sebastianvm.database.entities.ArtistWithAlbums
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {

    @Query(
        "SELECT * from ArtistEntity ORDER BY " +
            "CASE WHEN :sortOrder='ASCENDING' THEN name END COLLATE LOCALIZED ASC, " +
            "CASE WHEN :sortOrder='DESCENDING' THEN name END COLLATE LOCALIZED DESC")
    fun getArtists(sortOrder: String): Flow<List<ArtistEntity>>

    @Transaction
    @Query("SELECT * from ArtistEntity WHERE ArtistEntity.id=:artistId")
    fun getArtist(artistId: Long): Flow<ArtistWithAlbums>

    @Query("SELECT COUNT(*) FROM ArtistEntity") fun getArtistsCount(): Flow<Int>

    @Query("SELECT * FROM ArtistEntity WHERE ArtistEntity.id IN (:artistsIds)")
    fun getArtists(artistsIds: List<Long>): Flow<List<ArtistEntity>>

    @Transaction
    @Query(
        "SELECT ArtistEntity.* FROM ArtistEntity " +
            "INNER JOIN ArtistTrackCrossRef " +
            "ON ArtistEntity.id = ArtistTrackCrossRef.artistId " +
            "WHERE ArtistTrackCrossRef.trackId=:trackId " +
            "ORDER BY name COLLATE LOCALIZED ASC")
    fun getArtistsForTrack(trackId: Long): Flow<List<ArtistEntity>>

    @Transaction
    @Query(
        "SELECT ArtistEntity.* FROM ArtistEntity " +
            "INNER JOIN " +
            "(SELECT albumId, artistId FROM AlbumsForArtist UNION SELECT albumId, artistId FROM AppearsOnForArtist)" +
            "ON ArtistEntity.id = artistId " +
            "WHERE albumId=:albumId " +
            "ORDER BY name COLLATE LOCALIZED ASC")
    fun getArtistsForAlbum(albumId: Long): Flow<List<ArtistEntity>>
}
