package com.sebastianvm.musicplayer.repository.music

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MusicRepositoryModule {

    @Binds
    abstract fun bindMusicRepository(musicRepository: MusicRepositoryImpl): MusicRepository
}
