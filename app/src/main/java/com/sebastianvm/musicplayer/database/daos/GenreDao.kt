package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.sebastianvm.musicplayer.database.entities.GenreEntity
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Query("SELECT COUNT(*) FROM GenreEntity") fun getGenresCount(): Flow<Int>

    @Query(
        "SELECT * FROM GenreEntity ORDER BY " +
            "CASE WHEN :sortOrder='ASCENDING' THEN name END COLLATE LOCALIZED ASC, " +
            "CASE WHEN :sortOrder='DESCENDING' THEN name END COLLATE LOCALIZED DESC"
    )
    fun getGenres(sortOrder: MediaSortOrder): Flow<List<GenreEntity>>

    @Query("SELECT GenreEntity.name  FROM GenreEntity WHERE GenreEntity.id=:genreId")
    fun getGenreName(genreId: Long): Flow<String>
}
