package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.musicplayer.database.daos.ArtistDao
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import com.sebastianvm.musicplayer.player.HasArtists
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

class ArtistRepositoryImpl(
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val artistDao: ArtistDao,
) : ArtistRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getArtists(): Flow<List<Artist>> {
        return sortPreferencesRepository
            .getArtistListSortOrder()
            .flatMapLatest { sortOrder -> artistDao.getArtists(sortOrder = sortOrder) }
            .distinctUntilChanged()
    }

    override fun getArtist(artistId: Long): Flow<ArtistWithAlbums> {
        return artistDao.getArtist(artistId).distinctUntilChanged()
    }

    override fun getArtistsForMedia(media: HasArtists): Flow<List<Artist>> {
        return when (media) {
            is MediaGroup.SingleTrack -> {
                artistDao.getArtistsForTrack(media.trackId)
            }
            is MediaGroup.Album -> {
                artistDao.getArtistsForAlbum(media.albumId)
            }
        }.distinctUntilChanged()
    }
}
