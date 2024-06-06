package com.sebastianvm.musicplayer.di

import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepository
import com.sebastianvm.musicplayer.repository.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.genre.FakeGenreRepository
import com.sebastianvm.musicplayer.repository.music.MusicRepository
import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.repository.queue.FakeQueueRepository
import com.sebastianvm.musicplayer.repository.track.FakeTrackRepository

class FakeRepositoryProvider : RepositoryProvider {
    override val musicRepository: MusicRepository
        get() = TODO("Not yet implemented")

    override val albumRepository: FakeAlbumRepository = FakeAlbumRepository()

    override val artistRepository: FakeArtistRepository = FakeArtistRepository()

    override val genreRepository: FakeGenreRepository = FakeGenreRepository()

    override val trackRepository: FakeTrackRepository = FakeTrackRepository()

    override val playlistRepository: PlaylistRepository
        get() = TODO("Not yet implemented")

    override val playbackManager: FakePlaybackManager = FakePlaybackManager()

    override val searchRepository: FullTextSearchRepository
        get() = TODO("Not yet implemented")

    override val sortPreferencesRepository: FakeSortPreferencesRepository =
        FakeSortPreferencesRepository()

    override val queueRepository: FakeQueueRepository = FakeQueueRepository()
}
