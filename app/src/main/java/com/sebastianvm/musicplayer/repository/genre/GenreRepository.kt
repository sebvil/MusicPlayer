package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow

interface GenreRepository   {
    fun getGenresCount(): Flow<Int>
    fun getGenres(sortOrder: MediaSortOrder): Flow<List<Genre>>

}