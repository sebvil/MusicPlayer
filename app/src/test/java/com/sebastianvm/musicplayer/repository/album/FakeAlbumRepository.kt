//package com.sebastianvm.musicplayer.repository.album
//
//import com.sebastianvm.musicplayer.database.entities.Album
//import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.mapNotNull
//
//class FakeAlbumRepository(
//    private val fullAlbumInfo: List<FullAlbumInfo> = listOf(),
//) : AlbumRepository {
//
//    private val albumList = MutableStateFlow(fullAlbumInfo)
//
//    override fun getAlbumsCount(): Flow<Int> = albumList.map { it.size }
//
//    override fun getAlbums(): Flow<List<Album>> =
//        albumList.map { albums -> albums.map { it.album } }
//
//    override fun getAlbums(albumIds: List<String>): Flow<List<Album>> = albumList.map { albums ->
//        albums.mapNotNull { album -> album.takeIf { it.album.albumId in albumIds }?.album }.toList()
//    }
//
//    override fun getAlbum(albumId: String): Flow<FullAlbumInfo> = albumList.mapNotNull { albums -> albums.find { it.album.albumId == albumId } }
//}
