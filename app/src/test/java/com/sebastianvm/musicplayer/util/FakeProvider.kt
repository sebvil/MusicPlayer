package com.sebastianvm.musicplayer.util

import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepositoryImpl
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepositoryImpl
import com.sebastianvm.musicplayer.repository.genre.FakeGenreRepositoryImpl
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.repository.track.FakeTrackRepositoryImpl

object FakeProvider {
    val trackRepository: FakeTrackRepositoryImpl
        get() = FakeTrackRepositoryImpl()

    val sortPreferencesRepository: FakeSortPreferencesRepositoryImpl
        get() = FakeSortPreferencesRepositoryImpl()

    val albumRepository: FakeAlbumRepositoryImpl
        get() = FakeAlbumRepositoryImpl()

    val artistRepository: FakeArtistRepositoryImpl
        get() = FakeArtistRepositoryImpl()

    val genreRepository: FakeGenreRepositoryImpl
        get() = FakeGenreRepositoryImpl()
}
