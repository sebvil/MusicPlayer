package com.sebastianvm.musicplayer.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums


@Dao
interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArtists(
        artist: Artist
    ): Long


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArtists(
        artist: List<Artist>
    ): List<Long>

    @Transaction
    @Query("SELECT * from Artist")
    fun getArtists(): LiveData<List<ArtistWithAlbums>>

    @Query("SELECT COUNT(*) FROM Artist")
    fun getArtistsCount(): LiveData<Long>

    @Query("SELECT * FROM Artist WHERE artistGid = :artistGid")
    fun getArtist(artistGid: String): LiveData<Artist>


}