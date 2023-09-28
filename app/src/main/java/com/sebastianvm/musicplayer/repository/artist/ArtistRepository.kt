package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.fakegen.FakeQueryMethod
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import com.sebastianvm.musicplayer.model.MediaWithArtists
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    @FakeQueryMethod
    fun getArtists(): Flow<List<Artist>>

    @FakeQueryMethod
    fun getArtist(artistId: Long): Flow<ArtistWithAlbums>

    @FakeQueryMethod
    fun getArtistsForMedia(mediaType: MediaWithArtists, id: Long): Flow<List<Artist>>
}
