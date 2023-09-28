package com.sebastianvm.musicplayer.repository.genre

import com.sebastianvm.fakegen.FakeQueryMethod
import com.sebastianvm.musicplayer.database.entities.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    @FakeQueryMethod
    fun getGenres(): Flow<List<Genre>>

    @FakeQueryMethod
    fun getGenreName(genreId: Long): Flow<String>
}
