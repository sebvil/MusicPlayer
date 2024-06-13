package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.musicplayer.model.AlbumWithArtists
import com.sebastianvm.musicplayer.model.BasicAlbum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull

class FakeAlbumRepository : AlbumRepository {

    val albums: MutableStateFlow<List<AlbumWithArtists>> = MutableStateFlow(emptyList())

    override fun getAlbums(): Flow<List<AlbumWithArtists>> {
        return albums
    }

    override fun getAlbumWithArtists(albumId: Long): Flow<AlbumWithArtists> {
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
