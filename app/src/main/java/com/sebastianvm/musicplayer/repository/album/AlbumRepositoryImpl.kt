package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.model.Album
import com.sebastianvm.model.AlbumWithArtists
import com.sebastianvm.model.BasicAlbum
import com.sebastianvm.musicplayer.database.daos.AlbumDao
import com.sebastianvm.musicplayer.database.entities.asExternalModel
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
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
                    sortOption = sortPreferences.sortOption,
                    sortOrder = sortPreferences.sortOrder,
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
