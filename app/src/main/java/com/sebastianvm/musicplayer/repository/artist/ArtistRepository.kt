package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    fun getArtistsCount(): Flow<Int>
    fun getArtists(): Flow<List<Artist>>
    fun getArtist(artistId: Long): Flow<ArtistWithAlbums>
    fun getArtists(artistIds: List<Long>): Flow<List<Artist>>
}
