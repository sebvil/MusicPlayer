package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    fun getArtistsCount(): Flow<Int>
    fun getArtists(sortOrder: MediaSortOrder): Flow<List<Artist>>
    fun getArtist(artistName: String): Flow<ArtistWithAlbums>
    fun getArtist(artistId: Long): Flow<ArtistWithAlbums>
    fun getArtistsForTrack(trackId: String): Flow<List<Artist>>
    fun getArtistsForAlbum(albumId: String): Flow<List<Artist>>
}
