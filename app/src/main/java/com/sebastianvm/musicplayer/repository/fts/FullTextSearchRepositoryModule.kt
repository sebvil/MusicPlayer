package com.sebastianvm.musicplayer.repository.fts

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FullTextSearchRepositoryModule {

    @Binds
    abstract fun bindFullTextSearchRepository(
        fullTextSearchRepository: FullTextSearchRepositoryImpl
    ): FullTextSearchRepository
}
