package com.sebastianvm.musicplayer.core.data.di

import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
import com.sebastianvm.musicplayer.core.data.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.core.data.genre.GenreRepository
import com.sebastianvm.musicplayer.core.data.music.MusicRepository
import com.sebastianvm.musicplayer.core.data.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.data.queue.QueueRepository
import com.sebastianvm.musicplayer.core.data.track.TrackRepository

interface RepositoryProvider {
    val musicRepository: MusicRepository
    val albumRepository: AlbumRepository
    val artistRepository: ArtistRepository
    val genreRepository: GenreRepository
    val trackRepository: TrackRepository
    val playlistRepository: PlaylistRepository
    val searchRepository: FullTextSearchRepository
    val sortPreferencesRepository: SortPreferencesRepository
    val queueRepository: QueueRepository
}

interface HasRepositoryProvider {
    val repositoryProvider: RepositoryProvider
}
