package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumWithTracks
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getAlbumsCount(): Flow<Int>
    fun getAlbums(): Flow<List<Album>>
    fun getAlbum(albumId: Long): Flow<FullAlbumInfo>
    fun getAlbumWithTracks(albumId: Long): Flow<AlbumWithTracks>
}
