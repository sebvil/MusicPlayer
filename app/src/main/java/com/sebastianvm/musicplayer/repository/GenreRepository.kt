package com.sebastianvm.musicplayer.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.sebastianvm.musicplayer.database.daos.GenreDao
import com.sebastianvm.musicplayer.database.entities.GenreWithTracks
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenreRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val genreDao: GenreDao
) {
    fun getGenresCount(): Flow<Long> {
        return genreDao.getGenresCount()
    }

    fun getGenres(): Flow<List<GenreWithTracks>> {
        return genreDao.getGenres()
    }
}