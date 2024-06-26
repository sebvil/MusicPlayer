package com.sebastianvm.musicplayer.core.data.album

import com.sebastianvm.musicplayer.core.model.Album
import com.sebastianvm.musicplayer.core.model.AlbumWithArtists
import com.sebastianvm.musicplayer.core.model.BasicAlbum
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getAlbums(): Flow<List<AlbumWithArtists>>

    fun getAlbumWithArtists(albumId: Long): Flow<AlbumWithArtists>

    fun getAlbum(albumId: Long): Flow<Album>

    fun getBasicAlbum(albumId: Long): Flow<BasicAlbum>
}
