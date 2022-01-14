package com.sebastianvm.musicplayer.repository

import com.sebastianvm.musicplayer.database.daos.ArtistDao
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepository @Inject constructor(
    private val artistDao: ArtistDao
) {

    fun getArtistsCount(): Flow<Long> {
        return artistDao.getArtistsCount().distinctUntilChanged()
    }

    fun getArtistsWithAlbums(): Flow<List<ArtistWithAlbums>> {
        return artistDao.getArtistsWithAlbums().distinctUntilChanged()
    }

    fun getArtists(): Flow<List<Artist>> {
        return artistDao.getArtists().distinctUntilChanged()
    }
    fun getArtist(artistName: String): Flow<ArtistWithAlbums> {
        return artistDao.getArtist(artistName).distinctUntilChanged()

    }

}
