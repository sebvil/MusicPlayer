package com.sebastianvm.musicplayer.core.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.core.database.entities.GenreEntity
import com.sebastianvm.musicplayer.core.database.entities.GenreWithTracksEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Query("SELECT COUNT(*) FROM GenreEntity") fun getGenresCount(): Flow<Int>

    @Query(
        "SELECT * FROM GenreEntity ORDER BY " +
            "CASE WHEN :sortOrder='ASCENDING' THEN name END COLLATE LOCALIZED ASC, " +
            "CASE WHEN :sortOrder='DESCENDING' THEN name END COLLATE LOCALIZED DESC"
    )
    fun getGenres(sortOrder: String): Flow<List<GenreEntity>>

    @Transaction
    @Query("SELECT GenreEntity.*  FROM GenreEntity WHERE GenreEntity.id=:genreId")
    fun getGenre(genreId: Long): Flow<GenreWithTracksEntity>

    @Query("SELECT GenreEntity.name  FROM GenreEntity WHERE GenreEntity.id=:genreId")
    fun getGenreName(genreId: Long): Flow<String>
}
