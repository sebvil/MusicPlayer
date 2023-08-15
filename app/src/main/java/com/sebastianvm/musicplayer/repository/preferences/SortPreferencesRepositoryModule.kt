package com.sebastianvm.musicplayer.repository.preferences

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SortPreferencesRepositoryModule {

    @Binds
    abstract fun bindSortPreferencesRepository(source: SortPreferencesRepositoryImpl): SortPreferencesRepository
}
