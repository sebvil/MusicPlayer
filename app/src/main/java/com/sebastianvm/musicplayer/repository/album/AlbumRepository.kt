package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumWithTracks
import com.sebastianvm.musicplayer.database.entities.BasicAlbum
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getAlbums(): Flow<List<Album>>
    fun getFullAlbumInfo(albumId: Long): Flow<FullAlbumInfo>
    fun getAlbum(albumId: Long): Flow<BasicAlbum>
    fun getAlbumWithTracks(albumId: Long): Flow<AlbumWithTracks>
}
