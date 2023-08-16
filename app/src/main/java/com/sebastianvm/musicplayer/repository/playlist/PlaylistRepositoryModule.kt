package com.sebastianvm.musicplayer.repository.playlist

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface PlaylistRepositoryModule {
    @Binds
    fun bindPlaylistRepository(playlistRepository: PlaylistRepositoryImpl): PlaylistRepository
}
