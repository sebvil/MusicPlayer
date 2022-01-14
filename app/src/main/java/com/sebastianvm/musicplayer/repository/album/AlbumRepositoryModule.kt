package com.sebastianvm.musicplayer.repository.album

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AlbumRepositoryModule {

    @Binds
    abstract fun bindAlbumRepository(albumRepository: AlbumRepositoryImpl): AlbumRepository
}
