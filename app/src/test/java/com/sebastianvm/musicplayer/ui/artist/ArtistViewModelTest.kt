package com.sebastianvm.musicplayer.ui.artist

import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.database.entities.Fixtures
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.AlbumType
import com.sebastianvm.musicplayer.util.BaseTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ArtistViewModelTest : BaseTest() {

    private lateinit var artistRepository: ArtistRepository

    @Before
    fun setUp() {
        artistRepository = mockk {
            every { getArtist(C.ID_ONE) } returns flowOf(Fixtures.artistWithAlbums)
        }
    }

    private fun generateViewModel(): ArtistViewModel {
        return ArtistViewModel(
            initialState = ArtistState(
                artistId = C.ID_ONE,
                artistName = C.ARTIST_ANA,
                listItems = listOf()
            ),
            artistRepository = artistRepository
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state`() = testScope.runReliableTest {
        with(generateViewModel()) {
            assertEquals(
                listOf(
                    ArtistScreenItem.SectionHeaderItem(AlbumType.ALBUM),
                    ArtistScreenItem.AlbumRowItem(Fixtures.albumAlpaca.toModelListItemState()),
                    ArtistScreenItem.SectionHeaderItem(AlbumType.APPEARS_ON),
                    ArtistScreenItem.AlbumRowItem(Fixtures.albumCheetah.toModelListItemState()),
                    ArtistScreenItem.AlbumRowItem(Fixtures.albumBobcat.toModelListItemState())
                ),
                state.listItems
            )
        }
    }

    @Test
    fun `AlbumClicked navigates to album`() {
        with(generateViewModel()) {
            handle(ArtistUserAction.AlbumClicked(C.ID_ONE))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackList(
                            TrackListArgumentsForNav(
                                trackListType = TrackList.ALBUM,
                                trackListId = C.ID_ONE
                            )
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    @Test
    fun `AlbumOverflowMenuIconClicked navigates to Album context menu`() {
        with(generateViewModel()) {
            handle(ArtistUserAction.AlbumOverflowMenuIconClicked(C.ID_ONE))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.AlbumContextMenu(
                            AlbumContextMenuArguments(
                                albumId = C.ID_ONE
                            )
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    @Test
    fun `UpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            handle(ArtistUserAction.UpButtonClicked)
            assertEquals(listOf(NavEvent.NavigateUp), navEvents.value)
        }
    }
}
