package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.fakegen.FakeQueryMethod
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumWithTracks
import com.sebastianvm.musicplayer.database.entities.BasicAlbum
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    @FakeQueryMethod
    fun getAlbums(): Flow<List<Album>>

    @FakeQueryMethod
    fun getFullAlbumInfo(albumId: Long): Flow<FullAlbumInfo>

    @FakeQueryMethod
    fun getAlbum(albumId: Long): Flow<BasicAlbum>

    @FakeQueryMethod
    fun getAlbumWithTracks(albumId: Long): Flow<AlbumWithTracks>
}
