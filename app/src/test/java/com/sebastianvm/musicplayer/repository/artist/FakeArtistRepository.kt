package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeArtistRepository(
    artistsWithAlbums: List<ArtistWithAlbums> = listOf(),
) : ArtistRepository {

    private val artistWithAlbumsState = MutableStateFlow(artistsWithAlbums)


    override fun getArtistsCount(): Flow<Int> = artistWithAlbumsState.map { it.size }
    override fun getArtists(sortOrder: MediaSortOrder): Flow<List<Artist>> {
        return artistWithAlbumsState.map { artistsWithAlbums ->
            when (sortOrder) {
                MediaSortOrder.ASCENDING -> artistsWithAlbums.map { it.artist }
                    .sortedBy { it.artistName }
                MediaSortOrder.DESCENDING -> artistsWithAlbums.map { it.artist }
                    .sortedByDescending { it.artistName }
            }
        }
    }

    override fun getArtist(artistId: Long): Flow<ArtistWithAlbums> = TODO()

    override fun getArtistsForTrack(trackId: Long): Flow<List<Artist>> = TODO()

    override fun getArtistsForAlbum(albumId: Long): Flow<List<Artist>> = TODO()
}
