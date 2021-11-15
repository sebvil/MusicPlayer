package com.sebastianvm.musicplayer.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sebastianvm.musicplayer.database.entities.*

@Dao
interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlbum(album: Album): Long


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlbumForArtists(albumsForArtist: List<AlbumsForArtist>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAppearsOnForArtists(appearsOnForArtist: List<AppearsOnForArtist>)


    @Query("SELECT COUNT(*) FROM Album")
    suspend fun getAlbumsCount(): Long

    @Transaction
    @Query("SELECT * from Album WHERE Album.albumGid = :albumGid")
    suspend fun getAlbum(albumGid: String): AlbumWithArtists

    @Transaction
    @Query("SELECT * from Album WHERE Album.albumGid IN (:albumGids)")
    fun getAlbums(albumGids: List<String>): LiveData<List<AlbumWithArtists>>



    @Transaction
    @Query("SELECT * from Album")
    fun getAlbums(): LiveData<List<FullAlbumInfo>>


    @Transaction
    @Query(
        """
        SELECT Album.*
        FROM Album INNER JOIN AlbumsForArtist 
        ON Album.albumGid = AlbumsForArtist.albumGid 
        WHERE AlbumsForArtist.artistGid = :artistGid
        """
    )    fun getAlbumsForArtist(artistGid: String): LiveData<List<AlbumWithArtists>>


    @Transaction
    @Query(
        """
        SELECT Album.*
        FROM Album INNER JOIN AppearsOnForArtist 
        ON Album.albumGid = AppearsOnForArtist.albumGid 
        WHERE AppearsOnForArtist.artistGid = :artistGid
        """
    )
    fun getAppearsOnForArtist(artistGid: String): LiveData<List<AlbumWithArtists>>

    @Transaction
    @Query("SELECT * FROM Album WHERE Album.albumGid = :albumGid")
    fun getAlbumInfo(albumGid: String) : LiveData<FullAlbumInfo>

}