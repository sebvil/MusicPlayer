package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAlbumRepository(
    private val fullAlbumInfo: List<FullAlbumInfo> = listOf(),
) : AlbumRepository {
    override fun getAlbumsCount(): Flow<Long> = flow { emit(fullAlbumInfo.size.toLong()) }

    override fun getAlbums(): Flow<List<Album>> =
        flow { emit(fullAlbumInfo.map { it.album }.toList()) }

    override fun getAlbums(albumIds: List<String>): Flow<List<Album>> = flow {
        emit(fullAlbumInfo.mapNotNull { album -> album.takeIf { it.album.albumId in albumIds }?.album }
            .toList())
    }


    override fun getAlbum(albumId: String): Flow<FullAlbumInfo> = flow {
        fullAlbumInfo.find { it.album.albumId == albumId }?.also { emit(it) }
    }
}
