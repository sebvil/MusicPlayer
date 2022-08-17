package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.musicplayer.database.daos.ArtistDao
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val artistDao: ArtistDao
) : ArtistRepository {

    override fun getArtistsCount(): Flow<Int> {
        return artistDao.getArtistsCount().distinctUntilChanged()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getArtists(): Flow<List<Artist>> {
        return sortPreferencesRepository.getArtistListSortOrder().flatMapLatest { sortOrder ->
            artistDao.getArtists(sortOrder = sortOrder)
        }.distinctUntilChanged()
    }

    override fun getArtist(artistId: Long): Flow<ArtistWithAlbums> {
        return artistDao.getArtist(artistId).distinctUntilChanged()
    }

    override fun getArtistsForTrack(trackId: Long): Flow<List<Artist>> =
        artistDao.getArtistsForTrack(trackId).distinctUntilChanged()

    override fun getArtistsForAlbum(albumId: Long): Flow<List<Artist>> =
        artistDao.getArtistsForAlbum(albumId).distinctUntilChanged()
}
