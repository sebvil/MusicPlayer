package com.sebastianvm.musicplayer.repository.artist

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ArtistRepositoryModule {

    @Binds
    abstract fun bindArtistRepository(artistRepository: ArtistRepositoryImpl): ArtistRepository
}
