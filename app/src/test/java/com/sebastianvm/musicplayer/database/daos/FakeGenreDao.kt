package com.sebastianvm.musicplayer.database.daos

import com.sebastianvm.musicplayer.database.entities.Genre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGenreDao(private val genres: List<Genre> = listOf()) : GenreDao {
    override fun getGenresCount(): Flow<Int> = flow { emit(genres.size) }
    override fun getGenres(): Flow<List<Genre>> = flow { emit(genres) }
}