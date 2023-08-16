package com.sebastianvm.musicplayer.repository.genre

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface GenreRepositoryModule {
    @Binds
    fun bindGenreRepository(genreRepository: GenreRepositoryImpl): GenreRepository
}
