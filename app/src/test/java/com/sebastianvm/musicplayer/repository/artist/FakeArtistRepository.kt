package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.musicplayer.database.entities.AlbumBuilder
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistBuilder
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeArtistRepository : ArtistRepository {
    private val artist1 = ArtistBuilder.getDefaultArtist().build()

    private val album1 = AlbumBuilder.getDefaultAlbum().build()
    private val album2 = AlbumBuilder.getSecondaryAlbum().build()

    private val artistsWithAlbums = listOf(
        ArtistWithAlbums(
            artist = artist1,
            artistAlbums = listOf(album1),
            artistAppearsOn = listOf(album2)
        )
    )

    override fun getArtistsCount(): Flow<Long> = flow { emit(artistsWithAlbums.size.toLong()) }

    override fun getArtists(): Flow<List<Artist>> =
        flow { emit(artistsWithAlbums.map { it.artist }) }

    override fun getArtist(artistName: String): Flow<ArtistWithAlbums> =
        flow { artistsWithAlbums.find { it.artist.artistName == artistName }?.also { emit(it) } }
}
