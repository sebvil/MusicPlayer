package com.sebastianvm.musicplayer.repository.fts

import androidx.paging.PagingSource
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.database.entities.Track
import kotlinx.coroutines.flow.Flow

interface FullTextSearchRepository {
    fun searchTracksPaged(text: String): PagingSource<Int, FullTrackInfo>
    fun searchTracks(text: String): Flow<List<Track>>
    fun searchArtists(text: String): Flow<List<Artist>>
    fun searchAlbums(text: String): Flow<List<Album>>
    fun searchGenres(text: String): Flow<List<Genre>>
    fun searchPlaylists(text: String): Flow<List<Playlist>>
}