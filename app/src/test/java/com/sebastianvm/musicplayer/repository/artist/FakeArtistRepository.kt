package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import com.sebastianvm.musicplayer.repository.playback.mediaqueue.MediaQueueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeArtistRepository(
    private val artistsWithAlbums: List<ArtistWithAlbums> = listOf(),
    private val artistTrackCrossRef: List<ArtistTrackCrossRef> = listOf()
) :
    MediaQueueRepository {

    override fun getArtistsCount(): Flow<Int> = flow { emit(artistsWithAlbums.size) }

    override fun getArtists(): Flow<List<Artist>> =
        flow { emit(artistsWithAlbums.map { it.artist }) }

    override fun getArtist(artistName: String): Flow<ArtistWithAlbums> =
        flow { artistsWithAlbums.find { it.artist.artistName == artistName }?.also { emit(it) } }

    override fun getArtistsForTrack(trackId: String): Flow<List<Artist>> = flow {
        emit(artistsWithAlbums.filter {
            it.artist.artistName == artistTrackCrossRef.find { xref -> xref.trackId == trackId }?.artistName
        }.map { it.artist })
    }

    override fun getArtistsForAlbum(albumId: String): Flow<List<Artist>> = flow {
        emit(artistsWithAlbums.filter { albumId in it.artistAlbums }.map { it.artist })
    }
}
