package com.sebastianvm.musicplayer.core.data.genre

import com.sebastianvm.musicplayer.core.model.BasicGenre
import com.sebastianvm.musicplayer.core.model.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    fun getGenres(): Flow<List<BasicGenre>>

    fun getGenre(genreId: Long): Flow<Genre>

    fun getGenreName(genreId: Long): Flow<String>
}
