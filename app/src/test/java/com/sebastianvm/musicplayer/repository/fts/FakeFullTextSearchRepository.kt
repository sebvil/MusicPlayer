package com.sebastianvm.musicplayer.repository.fts

import com.sebastianvm.musicplayer.database.entities.BasicTrack
import com.sebastianvm.musicplayer.model.Album
import com.sebastianvm.musicplayer.model.BasicArtist
import com.sebastianvm.musicplayer.model.Genre
import com.sebastianvm.musicplayer.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeFullTextSearchRepository : FullTextSearchRepository {

    val trackQueryToResultsMap = MutableStateFlow(emptyMap<String, List<BasicTrack>>())
    val artistQueryToResultsMap = MutableStateFlow(emptyMap<String, List<BasicArtist>>())
    val albumQueryToResultsMap = MutableStateFlow(emptyMap<String, List<Album>>())
    val genreQueryToResultsMap = MutableStateFlow(emptyMap<String, List<Genre>>())
    val playlistQueryToResultsMap = MutableStateFlow(emptyMap<String, List<Playlist>>())

    override fun searchTracks(text: String): Flow<List<BasicTrack>> {
        return trackQueryToResultsMap.map { it[text].orEmpty() }
    }

    override fun searchArtists(text: String): Flow<List<BasicArtist>> {
        return artistQueryToResultsMap.map { it[text].orEmpty() }
    }

    override fun searchAlbums(text: String): Flow<List<Album>> {
        return albumQueryToResultsMap.map { it[text].orEmpty() }
    }

    override fun searchGenres(text: String): Flow<List<Genre>> {
        return genreQueryToResultsMap.map { it[text].orEmpty() }
    }

    override fun searchPlaylists(text: String): Flow<List<Playlist>> {
        return playlistQueryToResultsMap.map { it[text].orEmpty() }
    }
}
