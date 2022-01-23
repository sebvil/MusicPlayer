package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import kotlinx.coroutines.flow.Flow


@Dao
interface ArtistDao {

    @Query("SELECT * from Artist")
    fun getArtists(): Flow<List<Artist>>

    @Transaction
    @Query("SELECT * from Artist WHERE Artist.artistName=:artistName")
    fun getArtist(artistName: String): Flow<ArtistWithAlbums>

    @Query("SELECT COUNT(*) FROM Artist")
    fun getArtistsCount(): Flow<Long>
}
