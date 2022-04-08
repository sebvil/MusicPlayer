package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.musicplayer.database.daos.GenreDao
import com.sebastianvm.musicplayer.database.entities.Genre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class GenreRepositoryImpl @Inject constructor(private val genreDao: GenreDao) : GenreRepository {
    override fun getGenresCount(): Flow<Int> {
        return genreDao.getGenresCount().distinctUntilChanged()
    }

    override fun getGenres(): Flow<List<Genre>> {
        return genreDao.getGenres().distinctUntilChanged()
    }
}