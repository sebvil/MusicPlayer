package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.util.sort.AlbumListSortOptions
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT COUNT(*) FROM Album")
    fun getAlbumsCount(): Flow<Int>

    @Transaction
    @Query("SELECT * from Album WHERE Album.albumId IN (:albumIds)")
    fun getAlbums(albumIds: List<String>): Flow<List<Album>>

    @Transaction
    @Query("SELECT * from Album WHERE Album.albumId=:albumId")
    fun getAlbum(albumId: String): Flow<FullAlbumInfo>

    @Transaction
    @Query(
        "SELECT * from Album " +
                "JOIN Track ON Track.albumId=Album.albumId " +
                "WHERE Album.albumId=:albumId "
    )
    fun getAlbumWithTracks(albumId: String): Flow<Map<Album, List<FullTrackInfo>>>


    @Query(
        "SELECT * from Album ORDER BY " +
                "CASE WHEN:sortOption='ALBUM' AND :sortOrder='ASCENDING' THEN albumName END COLLATE LOCALIZED ASC, " +
                "CASE WHEN:sortOption='ALBUM' AND :sortOrder='DESCENDING' THEN albumName END COLLATE LOCALIZED DESC," +
                "CASE WHEN:sortOption='ARTIST' AND :sortOrder='ASCENDING' THEN artists END COLLATE LOCALIZED ASC, " +
                "CASE WHEN:sortOption='ARTIST' AND :sortOrder='DESCENDING' THEN artists END COLLATE LOCALIZED DESC, " +
                "CASE WHEN:sortOption='YEAR' AND :sortOrder='ASCENDING' THEN year END COLLATE LOCALIZED ASC, " +
                "CASE WHEN:sortOption='YEAR' AND :sortOrder='DESCENDING' THEN year END COLLATE LOCALIZED DESC"
    )
    fun getAllAlbums(sortOption: AlbumListSortOptions, sortOrder: MediaSortOrder): Flow<List<Album>>
}