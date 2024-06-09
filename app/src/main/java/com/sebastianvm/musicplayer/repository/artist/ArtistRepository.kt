package com.sebastianvm.musicplayer.repository.artist

import com.sebastianvm.musicplayer.model.Artist
import com.sebastianvm.musicplayer.model.BasicArtist
import com.sebastianvm.musicplayer.player.HasArtists
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    fun getArtists(): Flow<List<BasicArtist>>

    fun getArtist(artistId: Long): Flow<Artist>

    fun getArtistsForMedia(media: HasArtists): Flow<List<BasicArtist>>
}
