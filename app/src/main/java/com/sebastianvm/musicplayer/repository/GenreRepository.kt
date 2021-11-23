package com.sebastianvm.musicplayer.repository

import com.sebastianvm.musicplayer.database.daos.GenreDao
import com.sebastianvm.musicplayer.database.entities.GenreWithTracks
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenreRepository @Inject constructor(
    private val genreDao: GenreDao
) {
    fun getGenresCount(): Flow<Long> {
        return genreDao.getGenresCount()
    }

    fun getGenres(): Flow<List<GenreWithTracks>> {
        return genreDao.getGenres()
    }
}