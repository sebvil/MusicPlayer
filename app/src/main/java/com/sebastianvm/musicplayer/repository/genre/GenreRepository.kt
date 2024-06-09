package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.musicplayer.model.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    fun getGenres(): Flow<List<Genre>>

    fun getGenreName(genreId: Long): Flow<String>
}
