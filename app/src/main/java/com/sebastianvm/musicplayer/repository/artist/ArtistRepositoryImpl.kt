package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.database.daos.ArtistDao
import com.sebastianvm.database.entities.ArtistEntity
import com.sebastianvm.database.entities.ArtistWithAlbums
import com.sebastianvm.model.Artist
import com.sebastianvm.model.BasicArtist
import com.sebastianvm.musicplayer.player.HasArtists
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.album.asExternalModel
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class ArtistRepositoryImpl(
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val artistDao: ArtistDao,
) : ArtistRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getArtists(): Flow<List<BasicArtist>> {
        return sortPreferencesRepository
            .getArtistListSortOrder()
            .flatMapLatest { sortOrder -> artistDao.getArtists(sortOrder = sortOrder.name) }
            .map { artists -> artists.map { it.asExternalModel() } }
            .distinctUntilChanged()
    }

    override fun getArtist(artistId: Long): Flow<Artist> {
        return artistDao.getArtist(artistId).map { it.asExternalModel() }.distinctUntilChanged()
    }

    override fun getArtistsForMedia(media: HasArtists): Flow<List<BasicArtist>> {
        return when (media) {
                is MediaGroup.SingleTrack -> {
                    artistDao.getArtistsForTrack(media.trackId)
                }
                is MediaGroup.Album -> {
                    artistDao.getArtistsForAlbum(media.albumId)
                }
            }
            .map { artists -> artists.map { it.asExternalModel() } }
            .distinctUntilChanged()
    }
}

fun ArtistWithAlbums.asExternalModel(): Artist {
    return Artist(
        id = artist.id,
        name = artist.name,
        albums = artistAlbums.map { it.asExternalModel() },
        appearsOn = artistAppearsOn.map { it.asExternalModel() },
    )
}

fun ArtistEntity.asExternalModel() = BasicArtist(id = id, name = name)
