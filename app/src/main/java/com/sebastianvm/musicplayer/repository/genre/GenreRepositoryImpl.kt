package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.musicplayer.database.daos.GenreDao
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GenreRepositoryImpl @Inject constructor(
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val genreDao: GenreDao
) : GenreRepository {
    override fun getGenresCount(): Flow<Int> {
        return genreDao.getGenresCount().distinctUntilChanged()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getGenres(): Flow<List<Genre>> {
        return sortPreferencesRepository.getGenreListSortOrder()
            .flatMapLatest { sortOrder -> genreDao.getGenres(sortOrder = sortOrder) }
            .distinctUntilChanged()
    }

    override fun getGenreName(genreId: Long): Flow<String> {
        return genreDao.getGenreName(genreId = genreId).distinctUntilChanged()
    }
}
