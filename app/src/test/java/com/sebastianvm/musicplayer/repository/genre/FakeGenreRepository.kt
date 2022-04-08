package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.musicplayer.database.entities.Genre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeGenreRepository(genresList: List<Genre>) : GenreRepository {
    private val genres = MutableStateFlow(genresList)

    override fun getGenresCount(): Flow<Int> {
        return genres.map { it.count() }
    }

    override fun getGenres(): Flow<List<Genre>> {
        return genres
    }
}