package com.sebastianvm.musicplayer.core.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.sebastianvm.musicplayer.core.database.entities.AlbumWithArtistsEntity
import com.sebastianvm.musicplayer.core.database.entities.BasicAlbumQuery
import com.sebastianvm.musicplayer.core.database.entities.FullAlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * from AlbumEntity WHERE AlbumEntity.id=:albumId")
    fun getAlbumWithArtists(albumId: Long): Flow<AlbumWithArtistsEntity>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * from AlbumEntity WHERE AlbumEntity.id=:albumId")
    fun getAlbum(albumId: Long): Flow<FullAlbumEntity>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * from AlbumEntity WHERE AlbumEntity.id=:albumId")
    fun getBasicAlbum(albumId: Long): Flow<BasicAlbumQuery>

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
    fun getAllAlbums(sortOption: String, sortOrder: String): Flow<List<AlbumWithArtistsEntity>>
}
