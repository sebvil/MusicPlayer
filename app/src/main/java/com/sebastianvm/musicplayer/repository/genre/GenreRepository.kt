package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.musicplayer.model.BasicGenre
import com.sebastianvm.musicplayer.model.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    fun getGenres(): Flow<List<BasicGenre>>

    fun getGenre(genreId: Long): Flow<Genre>

    fun getGenreName(genreId: Long): Flow<String>
}
