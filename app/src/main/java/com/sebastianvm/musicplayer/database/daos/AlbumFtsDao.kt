package com.sebastianvm.musicplayer.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists

@Dao
interface AlbumFtsDao {
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT DISTINCT Album.* FROM Album " +
            "JOIN AlbumFts ON Album.id == AlbumFts.albumId " +
            "WHERE AlbumFts MATCH :text ORDER BY Album.albumName")
    fun albumsWithText(text: String): PagingSource<Int, AlbumWithArtists>
}