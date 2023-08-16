package com.sebastianvm.musicplayer.repository.fts

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FullTextSearchRepositoryModule {

    @Binds
    fun bindFullTextSearchRepository(
        fullTextSearchRepository: FullTextSearchRepositoryImpl
    ): FullTextSearchRepository
}
