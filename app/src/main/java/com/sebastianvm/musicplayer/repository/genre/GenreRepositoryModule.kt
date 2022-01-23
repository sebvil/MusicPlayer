package com.sebastianvm.musicplayer.repository.genre

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GenreRepositoryModule {
    @Binds
    abstract fun bindAlbumRepository(genreRepository: GenreRepositoryImpl): GenreRepository
}
