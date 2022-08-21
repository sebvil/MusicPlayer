package com.sebastianvm.musicplayer.ui.library.tracklist

import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.BaseTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AllTracksViewModelTest : BaseTest() {


    private fun generateViewModel(): AllTracksViewModel {
        return AllTracksViewModel(initialState = AllTracksState)
    }


    @Test
    fun `SortByClicked navigates to sort menu`() {
        with(generateViewModel()) {
            handle(AllTracksUserAction.SortByButtonClicked)
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.SortMenu(
                            SortMenuArguments(listType = SortableListType.Tracks(trackListType = TrackListType.ALL_TRACKS))
                        )
                    )
                ), navEvents.value
            )
        }
    }


    @Test
    fun `UpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            handle(AllTracksUserAction.UpButtonClicked)
            assertEquals(listOf(NavEvent.NavigateUp), navEvents.value)
        }
    }

}
