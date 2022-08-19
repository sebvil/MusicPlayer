package com.sebastianvm.musicplayer.repository.fts

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.BasicTrack
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.Playlist
import kotlinx.coroutines.flow.Flow

interface FullTextSearchRepository {
    fun searchTracks(text: String): Flow<List<BasicTrack>>
    fun searchArtists(text: String): Flow<List<Artist>>
    fun searchAlbums(text: String): Flow<List<Album>>
    fun searchGenres(text: String): Flow<List<Genre>>
    fun searchPlaylists(text: String): Flow<List<Playlist>>
}