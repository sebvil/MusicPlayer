package com.sebastianvm.musicplayer.ui.library.tracklist

import com.sebastianvm.musicplayer.FakeProvider
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManagerImpl
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.repository.track.FakeTrackRepositoryImpl
import com.sebastianvm.musicplayer.util.BaseTest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TrackListViewModelTest : BaseTest() {

    private lateinit var playbackManager: FakePlaybackManagerImpl
    private lateinit var trackRepository: FakeTrackRepositoryImpl
    private lateinit var sortPreferencesRepository: FakeSortPreferencesRepositoryImpl

    @BeforeEach
    fun beforeEach() {
        playbackManager = FakeProvider.playbackManager
        trackRepository = FakeProvider.trackRepository
        sortPreferencesRepository = FakeProvider.sortPreferencesRepository
    }

    private fun generateViewModel(trackList: TrackList = MediaGroup.AllTracks): TrackListViewModel {
        return TrackListViewModel(
            trackRepository = trackRepository,
            sortPreferencesRepository = sortPreferencesRepository,
            args = TrackListArguments(trackList),
            playbackManager = playbackManager
        )
    }

    @Test
    fun `test init`() = testScope.runTest {
        generateViewModel()
    }
}
