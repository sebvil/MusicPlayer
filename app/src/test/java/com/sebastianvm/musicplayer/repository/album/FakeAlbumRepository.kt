package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumWithTracks
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.BasicAlbum
import com.sebastianvm.musicplayer.database.entities.BasicTrack
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.database.entities.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull

class FakeAlbumRepository : AlbumRepository {

    val albums: MutableStateFlow<List<Album>> = MutableStateFlow(emptyList())
    val tracks: MutableStateFlow<List<Track>> = MutableStateFlow(emptyList())
    val albumsForArtist: MutableStateFlow<List<AlbumsForArtist>> = MutableStateFlow(emptyList())

    override fun getAlbums(): Flow<List<Album>> {
        return albums
    }

    override fun getFullAlbumInfo(albumId: Long): Flow<FullAlbumInfo> {
        return albums.mapNotNull { albumList ->
            val album = albumList.find { album -> album.id == albumId } ?: return@mapNotNull null
            val artistsForAlbum =
                albumsForArtist.value.filter { it.albumId == albumId }.map { it.artistId }
            val tracksInAlbum = tracks.value.filter { it.albumId == albumId }
            FullAlbumInfo(album = album, artists = artistsForAlbum, tracks = tracksInAlbum)
        }
    }

    override fun getAlbum(albumId: Long): Flow<BasicAlbum> {
        return albums.mapNotNull { albumList ->
            albumList.find { album -> album.id == albumId }?.let {
                BasicAlbum(
                    id = it.id,
                    albumName = it.albumName,
                    imageUri = it.imageUri,
                )
            }
        }
    }

    override fun getAlbumWithTracks(albumId: Long): Flow<AlbumWithTracks> {
        return albums.mapNotNull { albumList ->
            val album = albumList.find { album -> album.id == albumId } ?: return@mapNotNull null

            val tracksInAlbum = tracks.value.filter { it.albumId == albumId }
                .map {
                    BasicTrack(it.id, it.trackName, it.artists, it.trackNumber)
                }
            AlbumWithTracks(album = album, tracks = tracksInAlbum)
        }
    }
}
