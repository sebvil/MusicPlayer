package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import com.sebastianvm.musicplayer.model.MediaWithArtists
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull

class FakeArtistRepository : ArtistRepository {

    val artists: MutableStateFlow<List<Artist>> = MutableStateFlow(emptyList())
    val albumsForArtists: MutableStateFlow<List<AlbumsForArtist>> = MutableStateFlow(emptyList())
    val appearsOnForArtists: MutableStateFlow<List<AppearsOnForArtist>> =
        MutableStateFlow(emptyList())
    val albums: MutableStateFlow<List<Album>> = MutableStateFlow(emptyList())
    val artistTrackCrossRefs: MutableStateFlow<List<ArtistTrackCrossRef>> =
        MutableStateFlow(emptyList())

    override fun getArtists(): Flow<List<Artist>> {
        return artists
    }

    override fun getArtist(artistId: Long): Flow<ArtistWithAlbums> {
        return combine(
            artists,
            albumsForArtists,
            appearsOnForArtists,
            albums,
        ) { artists, albumsForArtists, appearsOnForArtists, albums ->
            val artist = artists.find { it.id == artistId } ?: return@combine null
            val albumsForArtist = albumsForArtists.filter { it.artistId == artistId }.mapNotNull {
                albums.find { album -> album.id == it.albumId }
            }
            val appearsOn = appearsOnForArtists.filter { it.artistId == artistId }.mapNotNull {
                albums.find { album -> album.id == it.albumId }
            }
            ArtistWithAlbums(artist, albumsForArtist, appearsOn)
        }.filterNotNull()
    }

    override fun getArtistsForMedia(mediaType: MediaWithArtists, id: Long): Flow<List<Artist>> {
        return when (mediaType) {
            MediaWithArtists.Album -> {
                combine(
                    artists,
                    albumsForArtists,
                    appearsOnForArtists
                ) { artists, albumsForArtists, appearsOnForArtists ->
                    albumsForArtists.filter { it.albumId == id }.flatMap { album ->
                        artists.filter { album.artistId == it.id }
                    }.ifEmpty {
                        appearsOnForArtists.filter { it.albumId == id }.flatMap { album ->
                            artists.filter { album.artistId == it.id }
                        }
                    }
                }
            }

            MediaWithArtists.Track -> {
                combine(artists, artistTrackCrossRefs) { artists, artistTrackCrossRefs ->

                    artistTrackCrossRefs.filter { it.trackId == id }
                        .flatMap { artistTrackCrossRef ->
                            artists.filter { it.id == artistTrackCrossRef.artistId }
                        }
                }
            }
        }
    }
}
