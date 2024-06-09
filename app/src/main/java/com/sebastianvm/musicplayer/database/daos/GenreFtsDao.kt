package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.sebastianvm.musicplayer.database.entities.GenreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreFtsDao {
    @Query(
        "SELECT * FROM GenreEntity JOIN GenreFts ON GenreEntity.name == GenreFts.name WHERE GenreFts.name MATCH :text"
    )
    fun genresWithText(text: String): Flow<List<GenreEntity>>
}
