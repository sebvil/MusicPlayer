package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.sebastianvm.musicplayer.database.entities.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreFtsDao {
    @Query("SELECT * FROM Genre JOIN GenreFts ON Genre.genreName == GenreFts.genreName WHERE GenreFts.genreName MATCH :text" )
    fun genresWithText(text: String): Flow<List<Genre>>
}