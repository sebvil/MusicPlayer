package com.sebastianvm.musicplayer

import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManagerImpl
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.repository.track.FakeTrackRepositoryImpl

object FakeProvider {

    val playbackManager: FakePlaybackManagerImpl
        get() = FakePlaybackManagerImpl()

    val trackRepository: FakeTrackRepositoryImpl
        get() = FakeTrackRepositoryImpl()

    val sortPreferencesRepository: FakeSortPreferencesRepositoryImpl
        get() = FakeSortPreferencesRepositoryImpl()
}
