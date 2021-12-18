package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Query("SELECT COUNT(*) FROM Genre")
    fun getGenresCount(): Flow<Long>

    @Transaction
    @Query("SELECT * FROM Genre")
    fun getGenresWithTracks(): Flow<List<GenreWithTracks>>

    @Transaction
    @Query("SELECT * FROM Genre WHERE Genre.genreName=:genreName")
    fun getGenreWithTracks(genreName: String): Flow<GenreWithTracks>

    @Query("SELECT * FROM Genre")
    fun getGenres(): Flow<List<Genre>>
}