package com.sebastianvm.musicplayer.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreWithTracks

@Dao
interface GenreDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGenre(genre: Genre): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGenres(genres: List<Genre>): List<Long>

    @Query("SELECT COUNT(*) FROM Genre")
    suspend fun getGenresCount(): Long

    @Transaction
    @Query("SELECT * FROM Genre")
    fun getGenres(): LiveData<List<GenreWithTracks>>
}