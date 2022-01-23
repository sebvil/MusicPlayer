package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.musicplayer.database.daos.ArtistDao
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val artistDao: ArtistDao
) : ArtistRepository {

    override fun getArtistsCount(): Flow<Long> {
        return artistDao.getArtistsCount().distinctUntilChanged()
    }

    override fun getArtists(): Flow<List<Artist>> {
        return artistDao.getArtists().distinctUntilChanged()
    }

    override fun getArtist(artistName: String): Flow<ArtistWithAlbums> {
        return artistDao.getArtist(artistName).distinctUntilChanged()
    }
}
