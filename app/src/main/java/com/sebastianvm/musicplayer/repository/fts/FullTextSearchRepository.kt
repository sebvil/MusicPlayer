package com.sebastianvm.musicplayer.repository.fts

import com.sebastianvm.model.AlbumWithArtists
import com.sebastianvm.model.BasicArtist
import com.sebastianvm.model.BasicGenre
import com.sebastianvm.model.BasicPlaylist
import com.sebastianvm.model.BasicTrack
import kotlinx.coroutines.flow.Flow

interface FullTextSearchRepository {
    fun searchTracks(text: String): Flow<List<BasicTrack>>

    fun searchArtists(text: String): Flow<List<BasicArtist>>

    fun searchAlbums(text: String): Flow<List<AlbumWithArtists>>

    fun searchGenres(text: String): Flow<List<BasicGenre>>

    fun searchPlaylists(text: String): Flow<List<BasicPlaylist>>
}
