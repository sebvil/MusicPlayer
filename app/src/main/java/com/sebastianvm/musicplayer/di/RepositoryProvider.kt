package com.sebastianvm.musicplayer.di

import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.music.MusicRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.queue.QueueRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository

interface RepositoryProvider {
    val musicRepository: MusicRepository
    val albumRepository: AlbumRepository
    val artistRepository: ArtistRepository
    val genreRepository: GenreRepository
    val trackRepository: TrackRepository
    val playlistRepository: PlaylistRepository
    val playbackManager: PlaybackManager
    val searchRepository: FullTextSearchRepository
    val sortPreferencesRepository: SortPreferencesRepository
    val queueRepository: QueueRepository
}
