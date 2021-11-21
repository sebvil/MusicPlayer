package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
       @Query("SELECT COUNT(*) FROM Album")
    fun getAlbumsCount(): Flow<Long>

    @Transaction
    @Query("SELECT * from Album WHERE Album.albumGid IN (:albumGids)")
    fun getAlbums(albumGids: List<String>): Flow<List<AlbumWithArtists>>



    @Transaction
    @Query("SELECT * from Album")
    fun getAlbums(): Flow<List<FullAlbumInfo>>
}