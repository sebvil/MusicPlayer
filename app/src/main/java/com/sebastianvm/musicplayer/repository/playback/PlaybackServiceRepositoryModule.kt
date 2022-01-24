package com.sebastianvm.musicplayer.repository.playback

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class PlaybackServiceRepositoryModule {

    @Binds
    abstract fun bindPlaybackRepository(playbackServiceRepository: PlaybackServiceRepositoryImpl): PlaybackServiceRepository
}
