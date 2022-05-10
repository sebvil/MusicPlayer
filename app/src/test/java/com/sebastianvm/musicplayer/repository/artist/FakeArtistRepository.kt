package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeArtistRepository(
    private val artistsWithAlbums: List<ArtistWithAlbums> = listOf(),
    private val artistTrackCrossRef: List<ArtistTrackCrossRef> = listOf()
) :
    ArtistRepository {

    override fun getArtistsCount(): Flow<Int> = flow { emit(artistsWithAlbums.size) }
    override fun getArtists(sortOrder: MediaSortOrder): Flow<List<Artist>> {
        TODO("Not yet implemented")
    }

    override fun getArtist(artistId: Long): Flow<ArtistWithAlbums> =
        flow { artistsWithAlbums.find { it.artist.id == artistId }?.also { emit(it) } }


    override fun getArtistsForTrack(trackId: Long): Flow<List<Artist>> = flow {
        emit(artistsWithAlbums.filter {
            it.artist.artistName == artistTrackCrossRef.find { xref -> xref.trackId == trackId }?.artistName
        }.map { it.artist })
    }

    override fun getArtistsForAlbum(albumId: Long): Flow<List<Artist>> = flow {
        emit(artistsWithAlbums.filter { albumId in it.artistAlbums }.map { it.artist })
    }
}
