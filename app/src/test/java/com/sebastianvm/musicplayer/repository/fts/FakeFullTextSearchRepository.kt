package com.sebastianvm.musicplayer.repository.fts

import com.sebastianvm.model.AlbumWithArtists
import com.sebastianvm.model.BasicArtist
import com.sebastianvm.model.BasicGenre
import com.sebastianvm.model.BasicPlaylist
import com.sebastianvm.model.BasicTrack
import com.sebastianvm.model.Track
import com.sebastianvm.musicplayer.util.extensions.mapValues
import com.sebastianvm.musicplayer.util.toBasicTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeFullTextSearchRepository : FullTextSearchRepository {

    val trackQueryToResultsMap = MutableStateFlow(emptyMap<String, List<Track>>())
    val artistQueryToResultsMap = MutableStateFlow(emptyMap<String, List<BasicArtist>>())
    val albumQueryToResultsMap = MutableStateFlow(emptyMap<String, List<AlbumWithArtists>>())
    val genreQueryToResultsMap = MutableStateFlow(emptyMap<String, List<BasicGenre>>())
    val playlistQueryToResultsMap = MutableStateFlow(emptyMap<String, List<BasicPlaylist>>())

    override fun searchTracks(text: String): Flow<List<BasicTrack>> {
        return trackQueryToResultsMap.map { it[text].orEmpty() }.mapValues { it.toBasicTrack() }
    }

    override fun searchArtists(text: String): Flow<List<BasicArtist>> {
        return artistQueryToResultsMap.map { it[text].orEmpty() }
    }

    override fun searchAlbums(text: String): Flow<List<AlbumWithArtists>> {
        return albumQueryToResultsMap.map { it[text].orEmpty() }
    }

    override fun searchGenres(text: String): Flow<List<BasicGenre>> {
        return genreQueryToResultsMap.map { it[text].orEmpty() }
    }

    override fun searchPlaylists(text: String): Flow<List<BasicPlaylist>> {
        return playlistQueryToResultsMap.map { it[text].orEmpty() }
    }
}
