package com.sebastianvm.musicplayer.core.data.fts

import com.sebastianvm.musicplayer.core.model.AlbumWithArtists
import com.sebastianvm.musicplayer.core.model.BasicArtist
import com.sebastianvm.musicplayer.core.model.BasicGenre
import com.sebastianvm.musicplayer.core.model.BasicPlaylist
import com.sebastianvm.musicplayer.core.model.BasicTrack
import kotlinx.coroutines.flow.Flow

interface FullTextSearchRepository {
    fun searchTracks(text: String): Flow<List<BasicTrack>>

    fun searchArtists(text: String): Flow<List<BasicArtist>>

    fun searchAlbums(text: String): Flow<List<AlbumWithArtists>>

    fun searchGenres(text: String): Flow<List<BasicGenre>>

    fun searchPlaylists(text: String): Flow<List<BasicPlaylist>>
}
