package com.sebastianvm.musicplayer.repository.playback

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

private const val PLAYBACK_INFO_PREFERENCES_FILE = "playback_info"

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaPlaybackRepositoryModule {
    @Binds
    abstract fun bindMediaPlaybackRepository(mediaPlaybackRepository: MediaPlaybackRepositoryImpl): MediaPlaybackRepository

}
