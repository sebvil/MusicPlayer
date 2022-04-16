package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Query("SELECT COUNT(*) FROM Genre")
    fun getGenresCount(): Flow<Int>

    @Query("SELECT * FROM Genre ORDER BY " +
            "CASE WHEN :sortOrder='ASCENDING' THEN genreName END ASC, " +
            "CASE WHEN :sortOrder='ASCENDING' THEN genreName END DESC")
    fun getGenresSorted(sortOrder: MediaSortOrder): Flow<List<Genre>>
}
