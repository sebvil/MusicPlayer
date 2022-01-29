package com.sebastianvm.musicplayer.repository.queue

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaQueueRepositoryModule {

    @Binds
    abstract fun bindMediaQueueRepository(mediaQueueRepository: MediaQueueRepositoryImpl): MediaQueueRepository
}
