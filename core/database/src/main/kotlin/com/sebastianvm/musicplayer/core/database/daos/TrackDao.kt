package com.sebastianvm.musicplayer.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.core.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.core.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.core.database.entities.ArtistEntity
import com.sebastianvm.musicplayer.core.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.DetailedTrack
import com.sebastianvm.musicplayer.core.database.entities.GenreEntity
import com.sebastianvm.musicplayer.core.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT COUNT(*) FROM TrackEntity") fun getTracksCount(): Flow<Int>

    @Transaction
    @Query(
        "SELECT * FROM TrackEntity ORDER BY " +
            "CASE WHEN:sortOption='TRACK' AND :sortOrder='ASCENDING' THEN trackName END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='TRACK' AND :sortOrder='DESCENDING' THEN trackName END COLLATE LOCALIZED DESC, " +
            "CASE WHEN:sortOption='ARTIST' AND :sortOrder='ASCENDING' THEN artists END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='ARTIST' AND :sortOrder='DESCENDING' THEN artists END COLLATE LOCALIZED DESC, " +
            "CASE WHEN:sortOption='ALBUM' AND :sortOrder='ASCENDING' THEN albumName END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='ALBUM' AND :sortOrder='DESCENDING' THEN albumName END COLLATE LOCALIZED DESC"
    )
    fun getAllTracks(sortOption: String, sortOrder: String): Flow<List<DetailedTrack>>

    @Transaction
    @Query("SELECT * FROM TrackEntity WHERE id=:trackId")
    fun getTrack(trackId: Long): Flow<DetailedTrack>

    @Transaction
    @Query(
        "SELECT TrackEntity.* FROM TrackEntity " +
            "INNER JOIN ArtistTrackCrossRef " +
            "ON TrackEntity.id = ArtistTrackCrossRef.trackId " +
            "WHERE ArtistTrackCrossRef.artistId=:artistId " +
            "ORDER BY trackName COLLATE LOCALIZED ASC"
    )
    fun getTracksForArtist(artistId: Long): Flow<List<DetailedTrack>>

    @Transaction
    @Query("SELECT * FROM TrackEntity WHERE TrackEntity.albumId=:albumId ORDER BY trackNumber")
    fun getTracksForAlbum(albumId: Long): Flow<List<DetailedTrack>>

    @Transaction
    @Query(
        "SELECT TrackEntity.* FROM TrackEntity " +
            "INNER JOIN GenreTrackCrossRef " +
            "ON TrackEntity.id = GenreTrackCrossRef.trackId " +
            "WHERE GenreTrackCrossRef.genreId=:genreId ORDER BY " +
            "CASE WHEN:sortOption='TRACK' AND :sortOrder='ASCENDING' THEN trackName END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='TRACK' AND :sortOrder='DESCENDING' THEN trackName END COLLATE LOCALIZED DESC, " +
            "CASE WHEN:sortOption='ARTIST' AND :sortOrder='ASCENDING' THEN artists END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='ARTIST' AND :sortOrder='DESCENDING' THEN artists END COLLATE LOCALIZED DESC, " +
            "CASE WHEN:sortOption='ALBUM' AND :sortOrder='ASCENDING' THEN albumName END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='ALBUM' AND :sortOrder='DESCENDING' THEN albumName END COLLATE LOCALIZED DESC"
    )
    fun getTracksForGenre(
        genreId: Long,
        sortOption: String,
        sortOrder: String,
    ): Flow<List<DetailedTrack>>

    @Transaction
    @Query(
        "SELECT TrackEntity.* FROM TrackEntity " +
            "INNER JOIN PlaylistTrackCrossRef " +
            "ON TrackEntity.id = PlaylistTrackCrossRef.trackId " +
            "WHERE PlaylistTrackCrossRef.playlistId=:playlistId ORDER BY " +
            "CASE WHEN:sortOption='CUSTOM' AND :sortOrder='ASCENDING' THEN position END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='CUSTOM' AND :sortOrder='DESCENDING' THEN position END COLLATE LOCALIZED DESC, " +
            "CASE WHEN:sortOption='TRACK' AND :sortOrder='ASCENDING' THEN trackName END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='TRACK' AND :sortOrder='DESCENDING' THEN trackName END COLLATE LOCALIZED DESC, " +
            "CASE WHEN:sortOption='ARTIST' AND :sortOrder='ASCENDING' THEN artists END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='ARTIST' AND :sortOrder='DESCENDING' THEN artists END COLLATE LOCALIZED DESC, " +
            "CASE WHEN:sortOption='ALBUM' AND :sortOrder='ASCENDING' THEN albumName END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='ALBUM' AND :sortOrder='DESCENDING' THEN albumName END COLLATE LOCALIZED DESC"
    )
    fun getTracksForPlaylist(
        playlistId: Long,
        sortOption: String,
        sortOrder: String,
    ): Flow<List<DetailedTrack>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllTracks(
        tracks: Set<TrackEntity>,
        artistTrackCrossRefs: Set<ArtistTrackCrossRef>,
        genreTrackCrossRefs: Set<GenreTrackCrossRef>,
        artists: Set<ArtistEntity>,
        genres: Set<GenreEntity>,
        albums: Set<com.sebastianvm.musicplayer.core.database.entities.AlbumEntity>,
        albumsForArtists: Set<AlbumsForArtist>,
        appearsOnForArtists: Set<AppearsOnForArtist>,
    )
}
