package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGenreRepository : GenreRepository {
    private val genres =
        listOf(GenreBuilder.getDefaultGenre().build(), GenreBuilder.getSecondaryGenre().build())

    override fun getGenresCount(): Flow<Long> = flow { emit(genres.size.toLong()) }
    override fun getGenres(): Flow<List<Genre>> = flow { emit(genres) }
}
