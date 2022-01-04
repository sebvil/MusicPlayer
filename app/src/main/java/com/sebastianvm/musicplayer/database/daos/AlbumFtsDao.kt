package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumFtsDao {
    @Transaction
    @Query("SELECT * FROM Album JOIN AlbumFts ON AlbumFts.albumId == AlbumFts.albumId WHERE AlbumFts.albumName MATCH :text")
    fun albumsWithText(text: String): Flow<List<FullAlbumInfo>>
}