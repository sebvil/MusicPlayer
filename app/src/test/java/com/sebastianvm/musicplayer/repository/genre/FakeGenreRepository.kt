package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeGenreRepository(genresList: List<Genre>) : GenreRepository {
    private val genresState = MutableStateFlow(genresList)

    override fun getGenresCount(): Flow<Int> {
        return genresState.map { it.count() }
    }

    override fun getGenres(sortOrder: MediaSortOrder): Flow<List<Genre>> {
        return genresState.map { genres ->
            when (sortOrder) {
                MediaSortOrder.ASCENDING -> genres.sortedBy { it.genreName }
                MediaSortOrder.DESCENDING -> genres.sortedByDescending { it.genreName }
            }
        }
    }

}