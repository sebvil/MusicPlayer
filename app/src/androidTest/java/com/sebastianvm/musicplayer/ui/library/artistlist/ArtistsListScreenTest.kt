package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test


class ArtistListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun generateViewModel(): ArtistListViewModel {
        return mockk(relaxed = true) {
            every { state } returns MutableStateFlow(
                ArtistListState(
                    listOf(
                        ArtistRowState("A", "A", shouldShowContextMenu = true),
                        ArtistRowState("B", "B", shouldShowContextMenu = true),
                        ArtistRowState("C", "C", shouldShowContextMenu = true),
                    ),
                    sortOrder = MediaSortOrder.ASCENDING
                )
            )
        }
    }


    @Test
    fun testArtistRowsAreShown() {
        // Start the app
        val screenViewModel = generateViewModel()
        composeTestRule.setContent {
            ArtistListScreen(
                screenViewModel = screenViewModel,
                delegate = mockk()
            )
        }

        composeTestRule.onNodeWithText("Artists").assertIsDisplayed()
        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onNodeWithText("B").assertIsDisplayed()
        composeTestRule.onNodeWithText("C").assertIsDisplayed()
    }

    @Test
    fun testArtistRowClicked() {
        // Start the app
        val screenViewModel = generateViewModel()
        composeTestRule.setContent {
            ArtistListScreen(
                screenViewModel = screenViewModel,
                delegate = mockk()
            )
        }
        composeTestRule.onNodeWithText("A").performClick()
        verify {
            screenViewModel.handle(ArtistListUserAction.ArtistClicked("A"))
        }
    }


}