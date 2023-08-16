package com.sebastianvm.musicplayer.repository.playback

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface MediaPlaybackRepositoryModule {
    @Binds
    fun bindMediaPlaybackRepository(mediaPlaybackRepository: PlaybackManagerImpl): PlaybackManager
}
