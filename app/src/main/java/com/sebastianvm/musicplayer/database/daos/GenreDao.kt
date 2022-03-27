package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.sebastianvm.musicplayer.database.entities.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Query("SELECT COUNT(*) FROM Genre")
    fun getGenresCount(): Flow<Int>

    @Query("SELECT * FROM Genre")
    fun getGenres(): Flow<List<Genre>>
}
