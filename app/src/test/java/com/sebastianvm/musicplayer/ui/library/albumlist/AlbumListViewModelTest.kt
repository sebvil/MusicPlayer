package com.sebastianvm.musicplayer.ui.library.albumlist

import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.database.entities.Fixtures
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.ui.album.AlbumArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.BaseTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class AlbumListViewModelTest : BaseTest() {

    private lateinit var albumRepository: AlbumRepository
    private val albums = listOf(
        Fixtures.albumAlpaca,
        Fixtures.albumBobcat,
        Fixtures.albumCheetah,
    )
    private val modelListItemStatesAscending = albums.map { it.toModelListItemState() }
    private val modelListItemStatesDescending = modelListItemStatesAscending.reversed()


    @Before
    fun setUp() {
        albumRepository = mockk {
            every { getAlbums() } returns emptyFlow()
        }
    }

    private fun generateViewModel(): AlbumListViewModel {

        return AlbumListViewModel(
            initialState = AlbumListState(albumList = listOf()),
            albumRepository = albumRepository,
        )
    }

    @Test
    fun `init sets initial state and updates state on change to album list`() =
        testScope.runReliableTest {
            val albumsFlow = MutableStateFlow(albums)
            every { albumRepository.getAlbums() } returns albumsFlow
            with(generateViewModel()) {
                advanceUntilIdle()
                assertEquals(modelListItemStatesAscending, state.value.albumList)
                albumsFlow.value = albums.reversed()
                advanceUntilIdle()
                assertEquals(modelListItemStatesDescending, state.value.albumList)
            }
        }

    @Test
    fun `AlbumClicked adds NavigateToAlbum event`() {
        with(generateViewModel()) {
            handle(AlbumListUserAction.AlbumClicked(C.ID_ONE))
            assertEquals(
                navEvents.value.first(),
                NavEvent.NavigateToScreen(
                    NavigationDestination.Album(
                        arguments = AlbumArguments(
                            albumId = C.ID_ONE
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `UpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            handle(AlbumListUserAction.UpButtonClicked)
            assertEquals(navEvents.value.first(), NavEvent.NavigateUp)
        }
    }

    @Test
    fun `SortByClicked navigates to SortMenu`() {
        with(generateViewModel()) {
            handle(AlbumListUserAction.SortByClicked)
            assertEquals(
                navEvents.value.first(),
                NavEvent.NavigateToScreen(
                    NavigationDestination.SortMenu(
                        arguments = SortMenuArguments(
                            listType = SortableListType.Albums
                        )
                    )
                )
            )
        }
    }


    @Test
    fun `onAlbumOverflowMenuIconClicked adds OpenContextMenu event`() {
        with(generateViewModel()) {
            handle(AlbumListUserAction.AlbumOverflowIconClicked(albumId = C.ID_ONE))
            assertEquals(
                navEvents.value.first(),
                NavEvent.NavigateToScreen(
                    NavigationDestination.AlbumContextMenu(
                        AlbumContextMenuArguments(albumId = C.ID_ONE)
                    )
                )
            )
        }
    }

}
