package com.sebastianvm.musicplayer.repository.playback

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaPlaybackRepositoryModule {

    @Binds
    abstract fun bindMediaPlaybackRepository(mediaPlaybackRepository: MediaPlaybackRepositoryImpl): MediaPlaybackRepository
}
