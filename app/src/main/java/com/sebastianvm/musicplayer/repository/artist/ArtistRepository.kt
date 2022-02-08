package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    fun getArtistsCount(): Flow<Long>
    fun getArtists(): Flow<List<Artist>>
    fun getArtist(artistName: String): Flow<ArtistWithAlbums>
}
