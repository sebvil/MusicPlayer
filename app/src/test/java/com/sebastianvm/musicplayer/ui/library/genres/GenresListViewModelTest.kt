package com.sebastianvm.musicplayer.ui.library.genres

import com.sebastianvm.musicplayer.player.BrowseTree
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.ui.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.ui.util.expectUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class GenresListViewModelTest {

    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private fun generateViewModel(musicServiceConnection: MusicServiceConnection = mock()): GenresListViewModel {
        return GenresListViewModel(
            initialState = mock(),
            genreRepository = mock(),
            preferencesRepository = mock(),
        )
    }

    @Test
    fun `init connects to service for genres root`() {
        val musicServiceConnection: MusicServiceConnection = mock()
        generateViewModel(musicServiceConnection)
        verify(musicServiceConnection).subscribe(
            eq(BrowseTree.GENRES_ROOT),
            any()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GenreClicked adds NavigateToGenre event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<GenresListUiEvent.NavigateToGenre>(this@runTest) {
                Assert.assertEquals(GENRE_NAME, genreName)
            }
            handle(GenresListUserAction.GenreClicked(GENRE_NAME))
        }
    }

    companion object {
        private const val GENRE_NAME = "GENRE_NAME"
    }
}