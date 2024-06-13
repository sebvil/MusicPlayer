package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.musicplayer.model.BasicGenre
import com.sebastianvm.musicplayer.model.Genre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class FakeGenreRepository : GenreRepository {

    val genres: MutableStateFlow<List<Genre>> = MutableStateFlow(emptyList())

    override fun getGenres(): Flow<List<BasicGenre>> {
        return genres
    }

    override fun getGenreName(genreId: Long): Flow<String> {
        return genres.map { genres -> genres.find { it.id == genreId }?.name }.filterNotNull()
    }
}
