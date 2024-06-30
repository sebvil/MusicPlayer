package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.musicplayer.core.database.daos.AlbumDao
import com.sebastianvm.musicplayer.core.database.entities.AlbumWithArtistsEntity
import com.sebastianvm.musicplayer.core.database.entities.BasicAlbumQuery
import com.sebastianvm.musicplayer.core.database.entities.FullAlbumEntity
import com.sebastianvm.musicplayer.core.model.Album
import com.sebastianvm.musicplayer.core.model.AlbumWithArtists
import com.sebastianvm.musicplayer.core.model.BasicAlbum
import com.sebastianvm.musicplayer.repository.artist.asExternalModel
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.asExternalModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class AlbumRepositoryImpl(
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val albumDao: AlbumDao,
) : AlbumRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAlbums(): Flow<List<AlbumWithArtists>> {
        return sortPreferencesRepository.getAlbumListSortPreferences().flatMapLatest {
            sortPreferences ->
            albumDao
                .getAllAlbums(
                    sortOption = sortPreferences.sortOption.name,
                    sortOrder = sortPreferences.sortOrder.name,
                )
                .map { albums -> albums.map { it.asExternalModel() } }
                .distinctUntilChanged()
        }
    }

    override fun getAlbumWithArtists(albumId: Long): Flow<AlbumWithArtists> {
        return albumDao
            .getAlbumWithArtists(albumId = albumId)
            .map { it.asExternalModel() }
            .distinctUntilChanged()
    }

    override fun getAlbum(albumId: Long): Flow<Album> {
        return albumDao.getAlbum(albumId = albumId).map { it.asExternalModel() }
    }

    override fun getBasicAlbum(albumId: Long): Flow<BasicAlbum> {
        return albumDao.getBasicAlbum(albumId = albumId).map { it.asExternalModel() }
    }
}

fun AlbumWithArtistsEntity.asExternalModel(): AlbumWithArtists {
    return AlbumWithArtists(
        id = album.id,
        title = album.title,
        imageUri = album.imageUri,
        artists = artists.map { it.asExternalModel() },
        year = album.year,
    )
}

fun BasicAlbumQuery.asExternalModel(): BasicAlbum {
    return BasicAlbum(id = id, title = title, imageUri = imageUri)
}

fun FullAlbumEntity.asExternalModel(): Album {
    return Album(
        id = album.id,
        title = album.title,
        imageUri = album.imageUri,
        artists = artists.map { it.asExternalModel() },
        year = album.year,
        tracks = tracks.map { it.asExternalModel() },
    )
}
