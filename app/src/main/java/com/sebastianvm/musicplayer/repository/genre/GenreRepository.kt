package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.model.BasicGenre
import com.sebastianvm.model.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    fun getGenres(): Flow<List<BasicGenre>>

    fun getGenre(genreId: Long): Flow<Genre>

    fun getGenreName(genreId: Long): Flow<String>
}
