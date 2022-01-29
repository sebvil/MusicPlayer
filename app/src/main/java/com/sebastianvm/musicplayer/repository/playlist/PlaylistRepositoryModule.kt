package com.sebastianvm.musicplayer.repository.playlist

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PlaylistRepositoryModule {
    @Binds
    abstract fun bindPlaylistRepository(playlistRepository: PlaylistRepositoryImpl): PlaylistRepository
}
