package com.sebastianvm.musicplayer.repository.track

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface TrackRepositoryModule {

    @Binds
    fun bindTrackRepository(trackRepository: TrackRepositoryImpl): TrackRepository
}
