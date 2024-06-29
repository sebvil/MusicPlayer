package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.model.Album
import com.sebastianvm.model.AlbumWithArtists
import com.sebastianvm.model.BasicAlbum
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getAlbums(): Flow<List<AlbumWithArtists>>

    fun getAlbumWithArtists(albumId: Long): Flow<AlbumWithArtists>

    fun getAlbum(albumId: Long): Flow<Album>

    fun getBasicAlbum(albumId: Long): Flow<BasicAlbum>
}
