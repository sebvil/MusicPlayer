package com.sebastianvm.musicplayer.repository.music

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface MusicRepositoryModule {

    @Binds
    fun bindMusicRepository(musicRepository: MusicRepositoryImpl): MusicRepository
}
