package com.sebastianvm.musicplayer.repository.fts

import com.sebastianvm.musicplayer.database.entities.BasicTrack
import com.sebastianvm.musicplayer.model.AlbumWithArtists
import com.sebastianvm.musicplayer.model.BasicArtist
import com.sebastianvm.musicplayer.model.BasicGenre
import com.sebastianvm.musicplayer.model.Playlist
import kotlinx.coroutines.flow.Flow

interface FullTextSearchRepository {
    fun searchTracks(text: String): Flow<List<BasicTrack>>

    fun searchArtists(text: String): Flow<List<BasicArtist>>

    fun searchAlbums(text: String): Flow<List<AlbumWithArtists>>

    fun searchGenres(text: String): Flow<List<BasicGenre>>

    fun searchPlaylists(text: String): Flow<List<Playlist>>
}
