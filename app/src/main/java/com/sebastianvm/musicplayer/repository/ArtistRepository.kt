package com.sebastianvm.musicplayer.repository

import com.sebastianvm.musicplayer.database.daos.ArtistDao
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepository @Inject constructor(
    private val artistDao: ArtistDao
) {

    fun getArtistsCount(): Flow<Long> {
        return artistDao.getArtistsCount()
    }

    fun getArtists(): Flow<List<ArtistWithAlbums>> {
        return artistDao.getArtists()
    }
}