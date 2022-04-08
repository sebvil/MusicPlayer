package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.musicplayer.database.entities.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository   {
    fun getGenresCount(): Flow<Int>
    fun getGenres(): Flow<List<Genre>>
}