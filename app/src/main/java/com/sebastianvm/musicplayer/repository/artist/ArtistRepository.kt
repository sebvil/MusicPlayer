package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import com.sebastianvm.musicplayer.model.MediaWithArtists
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    fun getArtists(): Flow<List<Artist>>

    fun getArtist(artistId: Long): Flow<ArtistWithAlbums>

    fun getArtistsForMedia(mediaType: MediaWithArtists, id: Long): Flow<List<Artist>>
}
