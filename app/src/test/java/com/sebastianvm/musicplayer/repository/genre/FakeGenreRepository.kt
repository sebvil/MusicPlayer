package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.musicplayer.core.common.extensions.mapValues
import com.sebastianvm.musicplayer.core.data.genre.GenreRepository
import com.sebastianvm.musicplayer.core.model.BasicGenre
import com.sebastianvm.musicplayer.core.model.Genre
import com.sebastianvm.musicplayer.util.toBasicGenre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class FakeGenreRepository : GenreRepository {

    val genres: MutableStateFlow<List<Genre>> = MutableStateFlow(emptyList())

    override fun getGenres(): Flow<List<BasicGenre>> {
        return genres.mapValues { it.toBasicGenre() }
    }

    override fun getGenre(genreId: Long): Flow<Genre> {
        return genres.map { genres -> genres.first { it.id == genreId } }
    }

    override fun getGenreName(genreId: Long): Flow<String> {
        return genres.map { genres -> genres.find { it.id == genreId }?.name }.filterNotNull()
    }
}
