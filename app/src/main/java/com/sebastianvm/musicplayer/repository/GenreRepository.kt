package com.sebastianvm.musicplayer.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.sebastianvm.musicplayer.database.daos.GenreDao
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreWithTracks
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenreRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val genreDao: GenreDao
) {

    suspend fun insertGenres(genreNames: List<String>): List<Long> {
        return genreDao.insertGenres(
            genreNames.map {
                Genre(it)
            }
        )
    }

    fun getGenresCount(): LiveData<Long> {
        return genreDao.getGenresCount()
    }

    fun getGenres(): LiveData<List<GenreWithTracks>> {
        return genreDao.getGenres()
    }
}