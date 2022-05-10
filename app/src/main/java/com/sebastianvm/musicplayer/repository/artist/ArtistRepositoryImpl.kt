package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.musicplayer.database.daos.ArtistDao
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val artistDao: ArtistDao
) : ArtistRepository {

    override fun getArtistsCount(): Flow<Int> {
        return artistDao.getArtistsCount().distinctUntilChanged()
    }

    override fun getArtists(sortOrder: MediaSortOrder): Flow<List<Artist>> {
        return artistDao.getArtists(sortOrder = sortOrder).distinctUntilChanged()
    }

    override fun getArtist(artistName: String): Flow<ArtistWithAlbums> {
        return artistDao.getArtist(artistName).distinctUntilChanged()
    }

    override fun getArtist(artistId: Long): Flow<ArtistWithAlbums> {
        return artistDao.getArtist(artistId).distinctUntilChanged()
    }

    override fun getArtistsForTrack(trackId: String): Flow<List<Artist>> =
        artistDao.getArtistsForTrack(trackId).distinctUntilChanged()

    override fun getArtistsForAlbum(albumId: Long): Flow<List<Artist>> =
        artistDao.getArtistsForAlbum(albumId).distinctUntilChanged()
}
