package com.sebastianvm.musicplayer.repository.playback

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PlaybackServiceRepositoryModule {

    @Binds
    abstract fun bindPlaybackRepository(playbackServiceRepository: PlaybackServiceRepositoryImpl): PlaybackServiceRepository
}
