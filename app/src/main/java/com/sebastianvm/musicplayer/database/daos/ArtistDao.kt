package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import kotlinx.coroutines.flow.Flow


@Dao
interface ArtistDao {
    @Transaction
    @Query("SELECT * from Artist")
    fun getArtists(): Flow<List<ArtistWithAlbums>>

    @Transaction
    @Query("SELECT * from Artist WHERE Artist.artistGid=:artistId")
    fun getArtist(artistId: String): Flow<ArtistWithAlbums>

    @Query("SELECT COUNT(*) FROM Artist")
    fun getArtistsCount(): Flow<Long>
}