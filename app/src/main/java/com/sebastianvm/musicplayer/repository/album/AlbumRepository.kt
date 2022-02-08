package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getAlbumsCount(): Flow<Long>
    fun getAlbums(): Flow<List<Album>>
    fun getAlbums(albumIds: List<String>): Flow<List<Album>>
    fun getAlbum(albumId: String): Flow<FullAlbumInfo>
}
