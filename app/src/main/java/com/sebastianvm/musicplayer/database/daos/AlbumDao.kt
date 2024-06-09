package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.AlbumEntity
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists
import com.sebastianvm.musicplayer.database.entities.BasicAlbumQuery
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT COUNT(*) FROM AlbumEntity") fun getAlbumsCount(): Flow<Int>

    @Transaction
    @Query("SELECT * from AlbumEntity WHERE AlbumEntity.id IN (:albumIds)")
    fun getAlbums(albumIds: List<Long>): Flow<List<AlbumEntity>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * from AlbumEntity WHERE AlbumEntity.id=:albumId")
    fun getFullAlbumInfo(albumId: Long): Flow<AlbumWithArtists>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * from AlbumEntity WHERE AlbumEntity.id=:albumId")
    fun getAlbum(albumId: Long): Flow<BasicAlbumQuery>

    @Transaction
    @Query(
        "SELECT * from AlbumEntity ORDER BY " +
            "CASE WHEN:sortOption='ALBUM' AND :sortOrder='ASCENDING' THEN title END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='ALBUM' AND :sortOrder='DESCENDING' THEN title END COLLATE LOCALIZED DESC," +
            "CASE WHEN:sortOption='ARTIST' AND :sortOrder='ASCENDING' THEN artists END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='ARTIST' AND :sortOrder='DESCENDING' THEN artists END COLLATE LOCALIZED DESC, " +
            "CASE WHEN:sortOption='YEAR' AND :sortOrder='ASCENDING' THEN year END COLLATE LOCALIZED ASC, " +
            "CASE WHEN:sortOption='YEAR' AND :sortOrder='DESCENDING' THEN year END COLLATE LOCALIZED DESC"
    )
    fun getAllAlbums(
        sortOption: SortOptions.AlbumListSortOptions,
        sortOrder: MediaSortOrder,
    ): Flow<List<AlbumWithArtists>>
}
