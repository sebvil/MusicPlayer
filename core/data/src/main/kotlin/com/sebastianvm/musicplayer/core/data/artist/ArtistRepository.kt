package com.sebastianvm.musicplayer.core.data.artist

import com.sebastianvm.musicplayer.core.model.Artist
import com.sebastianvm.musicplayer.core.model.BasicArtist
import com.sebastianvm.musicplayer.core.model.HasArtists
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    fun getArtists(): Flow<List<BasicArtist>>

    fun getArtist(artistId: Long): Flow<Artist>

    fun getArtistsForMedia(media: HasArtists): Flow<List<BasicArtist>>
}
