package com.sebastianvm.musicplayer.core.datatest.artist

import com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
import com.sebastianvm.musicplayer.core.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.core.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.core.model.Artist
import com.sebastianvm.musicplayer.core.model.BasicArtist
import com.sebastianvm.musicplayer.core.model.HasArtists
import com.sebastianvm.musicplayer.core.model.MediaGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class FakeArtistRepository : ArtistRepository {

    val artists: MutableStateFlow<List<Artist>> = MutableStateFlow(emptyList())
    val albumsForArtists: MutableStateFlow<List<AlbumsForArtist>> = MutableStateFlow(emptyList())
    val artistTrackCrossRefs: MutableStateFlow<List<ArtistTrackCrossRef>> =
        MutableStateFlow(emptyList())

    override fun getArtists(): Flow<List<BasicArtist>> {
        return artists.map { artists -> artists.map { BasicArtist(id = it.id, name = it.name) } }
    }

    override fun getArtist(artistId: Long): Flow<Artist> {
        return artists.mapNotNull { artists -> artists.find { it.id == artistId } }
    }

    override fun getArtistsForMedia(media: HasArtists): Flow<List<BasicArtist>> {
        return when (media) {
            is MediaGroup.Album -> {
                combine(artists, albumsForArtists) { artists, albumsForArtists ->
                    albumsForArtists
                        .filter { it.albumId == media.albumId }
                        .flatMap { album -> artists.filter { album.artistId == it.id } }
                        .distinct()
                }
            }
            is MediaGroup.SingleTrack -> {
                combine(artists, artistTrackCrossRefs) { artists, artistTrackCrossRefs ->
                    artistTrackCrossRefs
                        .filter { it.trackId == media.trackId }
                        .flatMap { artistTrackCrossRef ->
                            artists.filter { it.id == artistTrackCrossRef.artistId }
                        }
                        .distinct()
                }
            }
        }.map { artists -> artists.map { BasicArtist(id = it.id, name = it.name) } }
    }
}
