package com.sebastianvm.musicplayer.core.datatest.album

import com.sebastianvm.musicplayer.core.common.extensions.mapValues
import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.datatest.extensions.toAlbumWithArtists
import com.sebastianvm.musicplayer.core.model.Album
import com.sebastianvm.musicplayer.core.model.AlbumWithArtists
import com.sebastianvm.musicplayer.core.model.BasicAlbum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class FakeAlbumRepository : AlbumRepository {

    val albums: MutableStateFlow<List<Album>> = MutableStateFlow(emptyList())

    override fun getAlbums(): Flow<List<AlbumWithArtists>> {
        return albums.mapValues { it.toAlbumWithArtists() }
    }

    override fun getAlbumWithArtists(albumId: Long): Flow<AlbumWithArtists> {
        return albums
            .mapNotNull { albumList -> albumList.find { album -> album.id == albumId } }
            .map { it.toAlbumWithArtists() }
    }

    override fun getAlbum(albumId: Long): Flow<Album> {
        return albums.mapNotNull { albumList -> albumList.find { album -> album.id == albumId } }
    }

    override fun getBasicAlbum(albumId: Long): Flow<BasicAlbum> {
        return albums.mapNotNull { albumList ->
            albumList
                .find { album -> album.id == albumId }
                ?.let { BasicAlbum(id = it.id, title = it.title, imageUri = it.imageUri) }
        }
    }
}
