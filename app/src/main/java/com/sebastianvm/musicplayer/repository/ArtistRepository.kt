package com.sebastianvm.musicplayer.repository

import android.content.Context
import com.sebastianvm.musicplayer.database.daos.ArtistDao
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val artistDao: ArtistDao
) {

    fun getArtistsCount(): Flow<Long> {
        return artistDao.getArtistsCount()
    }

    fun getArtists(): Flow<List<ArtistWithAlbums>> {
        return artistDao.getArtists()
    }
}