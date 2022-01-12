package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT COUNT(*) FROM Album")
    fun getAlbumsCount(): Flow<Long>

    @Transaction
    @Query("SELECT * from Album WHERE Album.albumId IN (:albumIds)")
    fun getAlbums(albumIds: List<String>): Flow<List<AlbumWithArtists>>

    @Transaction
    @Query("SELECT * from Album WHERE Album.albumId=:albumId")
    fun getAlbum(albumId: String): Flow<FullAlbumInfo>

    @Transaction
    @Query("SELECT * from Album JOIN Track ON Track.albumId=Album.albumId WHERE Album.albumId=:albumId ")
    fun getAlbumWithTracks(albumId: String): Flow<Map<Album, List<FullTrackInfo>>>


    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * from Album")
    fun getAlbums(): Flow<List<AlbumWithArtists>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * from Album JOIN AlbumsForArtist ON Album.albumId = AlbumsForArtist.albumId WHERE AlbumsForArtist.artistId=:artistId")
    fun getAlbumsForArtist(artistId: String): Flow<List<AlbumWithArtists>>
}