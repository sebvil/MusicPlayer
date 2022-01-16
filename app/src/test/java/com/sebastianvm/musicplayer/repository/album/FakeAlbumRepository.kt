package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumBuilder
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists
import com.sebastianvm.musicplayer.database.entities.ArtistBuilder
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.GenreBuilder
import com.sebastianvm.musicplayer.database.entities.TrackBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAlbumRepository : AlbumRepository {

    private val album1 = AlbumBuilder.getDefaultAlbum().build()
    private val album2 = AlbumBuilder.getSecondaryAlbum().build()

    private val artist1 = ArtistBuilder.getDefaultArtist().build()
    private val artist2 = ArtistBuilder.getSecondaryArtist().build()

    private val track1 = TrackBuilder.getDefaultTrack().build()
    private val track2 = TrackBuilder.getSecondaryTrack().build()

    private val genre1 = GenreBuilder.getDefaultGenre().build()
    private val genre2 = GenreBuilder.getSecondaryGenre().build()


    private val fullAlbumInfoMap = mapOf(
        "1" to FullAlbumInfo(
            album = album1,
            artists = listOf(artist1),
            tracks = listOf(track1)
        ),
        "2" to FullAlbumInfo(
            album = album2,
            artists = listOf(artist2),
            tracks = listOf(track2)
        )
    )

    private val albumWithArtistsMap = mapOf(
        "1" to AlbumWithArtists(album1, listOf(artist1)),
        "2" to AlbumWithArtists(album2, listOf(artist2))
    )

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


    override fun getAlbumsCount(): Flow<Long> {
        return flow {
            emit(albumWithArtistsMap.size.toLong())
        }
    }

    override fun getAlbums(): Flow<List<AlbumWithArtists>> {
        return flow { emit(albumWithArtistsMap.values.toList()) }
    }

    override fun getAlbums(albumIds: List<String>): Flow<List<AlbumWithArtists>> {
        return flow {
            emit(albumWithArtistsMap.mapNotNull { entry -> entry.takeIf { it.key in albumIds }?.value }
                .toList())
        }
    }

    override fun getAlbum(albumId: String): Flow<FullAlbumInfo> {
        return flow {
            fullAlbumInfoMap[albumId]?.also {
                emit(it)
            }
        }
    }

    override fun getAlbumWithTracks(albumId: String): Flow<Map<Album, List<FullTrackInfo>>> {
        return flow {
            emit(albumWithTracksMap.filter { it.key.albumId == albumId })
        }
    }


}
