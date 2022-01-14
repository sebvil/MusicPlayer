package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {

    fun getAlbumsCount(): Flow<Long>

    fun getAlbums(): Flow<List<AlbumWithArtists>>

    fun getAlbums(albumIds: List<String>): Flow<List<AlbumWithArtists>>

    fun getAlbum(albumId: String): Flow<FullAlbumInfo>

    fun getAlbumWithTracks(albumId: String): Flow<Map<Album, List<FullTrackInfo>>>
}
