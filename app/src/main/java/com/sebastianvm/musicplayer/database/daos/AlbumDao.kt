package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT COUNT(*) FROM Album")
    fun getAlbumsCount(): Flow<Long>

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


    @Query("SELECT * from Album")
    fun getAllAlbums(): Flow<List<Album>>
}