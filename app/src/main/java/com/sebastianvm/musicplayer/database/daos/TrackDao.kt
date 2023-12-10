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
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackWithArtists
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT COUNT(*) FROM Track")
    fun getTracksCount(): Flow<Int>

    @Transaction
    @Query(
        "SELECT * FROM Track ORDER BY " +
                "CASE WHEN:sortOption='TRACK' AND :sortOrder='ASCENDING' THEN trackName END COLLATE LOCALIZED ASC, " +
                "CASE WHEN:sortOption='TRACK' AND :sortOrder='DESCENDING' THEN trackName END COLLATE LOCALIZED DESC, " +
                "CASE WHEN:sortOption='ARTIST' AND :sortOrder='ASCENDING' THEN artists END COLLATE LOCALIZED ASC, " +
                "CASE WHEN:sortOption='ARTIST' AND :sortOrder='DESCENDING' THEN artists END COLLATE LOCALIZED DESC, " +
                "CASE WHEN:sortOption='ALBUM' AND :sortOrder='ASCENDING' THEN albumName END COLLATE LOCALIZED ASC, " +
                "CASE WHEN:sortOption='ALBUM' AND :sortOrder='DESCENDING' THEN albumName END COLLATE LOCALIZED DESC"
    )
    fun getAllTracks(
        sortOption: SortOptions.TrackListSortOptions,
        sortOrder: MediaSortOrder
    ): Flow<List<Track>>

    @Transaction
    @Query("SELECT * FROM Track WHERE id=:trackId")
    fun getTrack(trackId: Long): Flow<TrackWithArtists>

    @Transaction
    @Query(
        "SELECT Track.* FROM Track " +
                "INNER JOIN ArtistTrackCrossRef " +
                "ON Track.id = ArtistTrackCrossRef.trackId " +
                "WHERE ArtistTrackCrossRef.artistId=:artistId " +
                "ORDER BY trackName COLLATE LOCALIZED ASC"
    )
    fun getTracksForArtist(artistId: Long): Flow<List<Track>>

    @Transaction
    @Query("SELECT * FROM Track WHERE Track.albumId=:albumId ORDER BY trackNumber")
    fun getTracksForAlbum(albumId: Long): Flow<List<Track>>

    @Transaction
    @Query(
        "SELECT Track.* FROM Track " +
                "INNER JOIN GenreTrackCrossRef " +
                "ON Track.id = GenreTrackCrossRef.trackId " +
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
        sortOption: SortOptions.TrackListSortOptions,
        sortOrder: MediaSortOrder
    ): Flow<List<Track>>

    @Query(
        "SELECT Track.* FROM Track " +
                "INNER JOIN PlaylistTrackCrossRef " +
                "ON Track.id = PlaylistTrackCrossRef.trackId " +
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
        sortOption: SortOptions.PlaylistSortOptions,
        sortOrder: MediaSortOrder
    ): Flow<List<Track>>

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
