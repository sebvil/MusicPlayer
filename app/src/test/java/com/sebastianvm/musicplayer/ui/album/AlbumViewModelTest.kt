package com.sebastianvm.musicplayer.ui.album

import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.database.entities.Fixtures
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.util.BaseTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumViewModelTest : BaseTest() {

    private lateinit var playbackManager: PlaybackManager
    private lateinit var albumRepository: AlbumRepository


    @Before
    fun setUp() {
        playbackManager = mockk()
        albumRepository = mockk {
            every { getAlbumWithTracks(C.ID_ONE) } returns MutableStateFlow(Fixtures.albumWithTracks)
        }
    }

    private fun generateViewModel(): AlbumViewModel {
        return AlbumViewModel(
            initialState = AlbumState(
                albumId = C.ID_ONE,
                albumName = "",
                imageUri = "",
            ),
            albumRepository = albumRepository,
        )
    }

    @Test
    fun `init sets initial state`() =
        testScope.runReliableTest {
            with(generateViewModel()) {
                advanceUntilIdle()
                assertEquals(C.ALBUM_ALPACA, state.value.albumName)
            }
        }
}
