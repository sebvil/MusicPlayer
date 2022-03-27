package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.musicplayer.database.entities.Genre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGenreRepository(private val genres: List<Genre> = listOf()) : GenreRepository {
    override fun getGenresCount(): Flow<Int> = flow { emit(genres.size.toLong()) }
    override fun getGenres(): Flow<List<Genre>> = flow { emit(genres) }
}
