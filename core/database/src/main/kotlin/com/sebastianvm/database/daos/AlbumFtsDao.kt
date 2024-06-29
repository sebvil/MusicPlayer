package com.sebastianvm.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.sebastianvm.database.entities.AlbumWithArtistsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumFtsDao {
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT DISTINCT AlbumEntity.* FROM AlbumEntity " +
            "JOIN AlbumFts ON AlbumEntity.id == AlbumFts.albumId " +
            "WHERE AlbumFts MATCH :text ORDER BY AlbumEntity.title")
    fun albumsWithText(text: String): Flow<List<AlbumWithArtistsEntity>>
}
