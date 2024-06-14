package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.musicplayer.database.daos.GenreDao
import com.sebastianvm.musicplayer.database.entities.asExternalModel
import com.sebastianvm.musicplayer.model.BasicGenre
import com.sebastianvm.musicplayer.model.Genre
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.util.extensions.mapValues
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class GenreRepositoryImpl(
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val genreDao: GenreDao,
) : GenreRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getGenres(): Flow<List<BasicGenre>> {
        return sortPreferencesRepository
            .getGenreListSortOrder()
            .flatMapLatest { sortOrder -> genreDao.getGenres(sortOrder = sortOrder) }
            .mapValues { it.asExternalModel() }
            .distinctUntilChanged()
    }

    override fun getGenre(genreId: Long): Flow<Genre> {
        return genreDao.getGenre(genreId = genreId).map { it.asExternalModel() }
    }

    override fun getGenreName(genreId: Long): Flow<String> {
        return genreDao.getGenreName(genreId = genreId).distinctUntilChanged()
    }
}
