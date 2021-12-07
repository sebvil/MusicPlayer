package com.sebastianvm.musicplayer.ui.library.artists

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test


class ArtistsListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun generateViewModel(): ArtistsListViewModel {
        return mockk(relaxed = true) {
            every { state } returns MutableStateFlow(ArtistsListState(
                listOf(
                    ArtistsListItem("A", "A"),
                    ArtistsListItem("B", "B"),
                    ArtistsListItem("C", "C"),
                )
            ))
        }
    }


    @Test
    fun testArtistRowsAreShown() {
        // Start the app
        val screenViewModel = generateViewModel()
        composeTestRule.setContent {
            ArtistsListScreen(
                screenViewModel = screenViewModel,
                bottomNavBar = { },
                navigateToArtist = { _, _ -> }
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
            ArtistsListScreen(
                screenViewModel = screenViewModel,
                bottomNavBar = { },
                navigateToArtist = { _, _ -> }
            )
        }
        composeTestRule.onNodeWithText("A").performClick()
        verify {
            screenViewModel.handle(ArtistsListUserAction.ArtistClicked("A", "A"))
        }
    }

}