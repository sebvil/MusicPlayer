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
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.TrackListSortOptions
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
        sortOption: TrackListSortOptions,
        sortOrder: MediaSortOrder
    ): Flow<List<Track>>

    @Transaction
    @Query("SELECT * FROM Track WHERE trackId in (:trackIds)")
    fun getTracks(trackIds: List<String>): Flow<List<Track>>

    @Transaction
    @Query("SELECT * FROM Track WHERE trackId=:trackId")
    fun getTrack(trackId: String): Flow<FullTrackInfo>

    @Transaction
    @Query(
        """
        SELECT Track.* FROM Track 
        INNER JOIN ArtistTrackCrossRef ON Track.trackId = ArtistTrackCrossRef.trackId
        WHERE ArtistTrackCrossRef.artistName=:artistName
    """
    )
    fun getTracksForArtist(artistName: String): Flow<List<Track>>

    @Transaction
    @Query(
        """
        SELECT * FROM Track 
        WHERE Track.albumId=:albumId
    """
    )
    fun getTracksForAlbum(albumId: String): Flow<List<Track>>

    @Transaction
    @Query(
        "SELECT Track.* FROM Track " +
                "INNER JOIN GenreTrackCrossRef " +
                "ON Track.trackId = GenreTrackCrossRef.trackId " +
                "WHERE GenreTrackCrossRef.genreName=:genreName ORDER BY " +
                "CASE WHEN:sortOption='TRACK' AND :sortOrder='ASCENDING' THEN trackName END COLLATE LOCALIZED ASC, " +
                "CASE WHEN:sortOption='TRACK' AND :sortOrder='DESCENDING' THEN trackName END COLLATE LOCALIZED DESC, " +
                "CASE WHEN:sortOption='ARTIST' AND :sortOrder='ASCENDING' THEN artists END COLLATE LOCALIZED ASC, " +
                "CASE WHEN:sortOption='ARTIST' AND :sortOrder='DESCENDING' THEN artists END COLLATE LOCALIZED DESC, " +
                "CASE WHEN:sortOption='ALBUM' AND :sortOrder='ASCENDING' THEN albumName END COLLATE LOCALIZED ASC, " +
                "CASE WHEN:sortOption='ALBUM' AND :sortOrder='DESCENDING' THEN albumName END COLLATE LOCALIZED DESC"
    )
    fun getTracksForGenre(
        genreName: String,
        sortOption: TrackListSortOptions,
        sortOrder: MediaSortOrder
    ): Flow<List<Track>>

    @Transaction
    @Query(
        """
        SELECT Track.* FROM Track 
        INNER JOIN PlaylistTrackCrossRef ON Track.trackId = PlaylistTrackCrossRef.trackId
        WHERE PlaylistTrackCrossRef.playlistName=:playlistName
    """
    )
    fun getTracksForPlaylist(playlistName: String): Flow<List<Track>>

    @Transaction
    @Query(
        """
        SELECT Track.* FROM Track 
        INNER JOIN MediaQueueTrackCrossRef ON Track.trackId = MediaQueueTrackCrossRef.trackId
        WHERE MediaQueueTrackCrossRef.mediaGroupType=:mediaType AND MediaQueueTrackCrossRef.groupMediaId=:groupMediaId 
        ORDER BY MediaQueueTrackCrossRef.trackIndex ASC
    """
    )
    fun getTracksForQueue(mediaType: MediaGroupType, groupMediaId: String): Flow<List<Track>>

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
