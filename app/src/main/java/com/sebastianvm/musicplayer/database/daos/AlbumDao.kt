package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumWithTracks
import com.sebastianvm.musicplayer.database.entities.BasicAlbum
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT COUNT(*) FROM Album")
    fun getAlbumsCount(): Flow<Int>

    @Transaction
    @Query("SELECT * from Album WHERE Album.id IN (:albumIds)")
    fun getAlbums(albumIds: List<Long>): Flow<List<Album>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * from Album WHERE Album.id=:albumId")
    fun getFullAlbumInfo(albumId: Long): Flow<FullAlbumInfo>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * from Album WHERE Album.id=:albumId")
    fun getAlbum(albumId: Long): Flow<BasicAlbum>

    @Transaction
    @Query("SELECT * from Album WHERE Album.id=:albumId")
    fun getAlbumWithTracks(albumId: Long): Flow<AlbumWithTracks>

    @Query(
        "SELECT * from Album ORDER BY " +
            "CASE WHEN:sortOption='ALBUM' AND :sortOrder='ASCENDING' THEN albumName END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='ALBUM' AND :sortOrder='DESCENDING' THEN albumName END COLLATE LOCALIZED DESC," +
            "CASE WHEN:sortOption='ARTIST' AND :sortOrder='ASCENDING' THEN artists END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='ARTIST' AND :sortOrder='DESCENDING' THEN artists END COLLATE LOCALIZED DESC, " +
            "CASE WHEN:sortOption='YEAR' AND :sortOrder='ASCENDING' THEN year END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='YEAR' AND :sortOrder='DESCENDING' THEN year END COLLATE LOCALIZED DESC"
    )
    fun getAllAlbums(
        sortOption: SortOptions.AlbumListSortOptions,
        sortOrder: MediaSortOrder
    ): Flow<List<Album>>
}
