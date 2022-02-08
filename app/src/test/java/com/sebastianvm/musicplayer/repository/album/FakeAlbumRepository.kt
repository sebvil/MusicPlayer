package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumBuilder
import com.sebastianvm.musicplayer.database.entities.ArtistBuilder
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.GenreBuilder
import com.sebastianvm.musicplayer.database.entities.TrackBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAlbumRepository(private val fullAlbumInfo: List<FullAlbumInfo>, private val fullTrackInfo: List<FullTrackInfo>) : AlbumRepository {

    private val album1 = AlbumBuilder.getDefaultAlbum().build()
    private val album2 = AlbumBuilder.getSecondaryAlbum().build()

    private val artist1 = ArtistBuilder.getDefaultArtist().build()
    private val artist2 = ArtistBuilder.getSecondaryArtist().build()

    private val track1 = TrackBuilder.getDefaultTrack().build()
    private val track2 = TrackBuilder.getSecondaryTrack().build()

    private val genre1 = GenreBuilder.getDefaultGenre().build()
    private val genre2 = GenreBuilder.getSecondaryGenre().build()

    private val albumWithTracksMap = mapOf(
        album1 to listOf(
            FullTrackInfo(
                track = track1,
                artists = listOf(artist1),
                genres = listOf(genre1),
                album = album1,
            )
        ),
        album2 to listOf(
            FullTrackInfo(
                track = track2,
                artists = listOf(artist2),
                genres = listOf(genre2),
                album = album2,
            )
        )
    )


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


    override fun getAlbumWithTracks(albumId: String): Flow<Map<Album, List<FullTrackInfo>>> {
        return flow {
            emit(albumWithTracksMap.filter { it.key.albumId == albumId })
        }
    }


}
