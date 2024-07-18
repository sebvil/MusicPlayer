package com.sebastianvm.musicplayer.core.data.di

import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.data.track.TrackRepository

interface RepositoryProvider {
    val musicRepository: com.sebastianvm.musicplayer.core.data.music.MusicRepository
    val albumRepository: com.sebastianvm.musicplayer.core.data.album.AlbumRepository
    val artistRepository: com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
    val genreRepository: com.sebastianvm.musicplayer.core.data.genre.GenreRepository
    val trackRepository: TrackRepository
    val playlistRepository: com.sebastianvm.musicplayer.core.data.playlist.PlaylistRepository
    val searchRepository: com.sebastianvm.musicplayer.core.data.fts.FullTextSearchRepository
    val sortPreferencesRepository: SortPreferencesRepository
    val queueRepository: com.sebastianvm.musicplayer.core.data.queue.QueueRepository
}
