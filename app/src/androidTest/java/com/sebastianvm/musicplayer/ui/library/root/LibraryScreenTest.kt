package com.sebastianvm.musicplayer.ui.library.root

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.library.root.listitem.LibraryItem
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LibraryScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var searchText: String
    private lateinit var libraryText: String
    private lateinit var scanText: String

    private lateinit var allSongsText: String
    private lateinit var artistsText: String
    private lateinit var albumsText: String
    private lateinit var genresText: String
    private lateinit var playlistsText: String

    private lateinit var allSongsCountText: String
    private lateinit var artistsCountText: String
    private lateinit var albumsCountText: String
    private lateinit var genresCountText: String
    private lateinit var playlistsCountText: String

    @Before
    fun setup() {
        composeTestRule.activity.apply {
            searchText = getString(R.string.search)
            libraryText = getString(R.string.library)
            scanText = getString(R.string.scan)

            allSongsText = getString(R.string.all_songs)
            artistsText = getString(R.string.artists)
            albumsText = getString(R.string.albums)
            genresText = getString(R.string.genres)
            playlistsText = getString(R.string.playlists)
        }
    }


    @Test
    fun libraryScreen_withZeroCounts_showsContent() {
        composeTestRule.setContent {
            TestLibraryScreen(state = newLibraryState())
        }
        assertScreenContent()
    }

    @Test
    fun libraryScreen_withOneCounts_showsContent() {
        composeTestRule.setContent {
            TestLibraryScreen(
                state = newLibraryState(
                    trackCount = 1,
                    artistCount = 1,
                    albumCount = 1,
                    genreCount = 1,
                    playlistCount = 1
                )
            )
        }
        assertScreenContent()
    }

    @Test
    fun trackItemClicked_navigatesToTrackList() {
        var didNavigate = false
        composeTestRule.setContent {
            TestLibraryScreen(
                state = newLibraryState(),
                navigateToAllTracksList = { didNavigate = true })
        }
        composeTestRule.onNodeWithText(allSongsText).performClick()
        assertTrue(didNavigate)
    }

    @Test
    fun artistItemClicked_navigatesToTrackList() {
        var didNavigate = false
        composeTestRule.setContent {
            TestLibraryScreen(
                state = newLibraryState(),
                navigateToArtistList = { didNavigate = true })
        }
        composeTestRule.onNodeWithText(artistsText).performClick()
        assertTrue(didNavigate)
    }

    @Test
    fun albumItemClicked_navigatesToTrackList() {
        var didNavigate = false
        composeTestRule.setContent {
            TestLibraryScreen(
                state = newLibraryState(),
                navigateToAlbumList = { didNavigate = true })
        }
        composeTestRule.onNodeWithText(albumsText).performClick()
        assertTrue(didNavigate)
    }

    @Test
    fun genreItemClicked_navigatesToTrackList() {
        var didNavigate = false
        composeTestRule.setContent {
            TestLibraryScreen(
                state = newLibraryState(),
                navigateToGenreList = { didNavigate = true })
        }
        composeTestRule.onNodeWithText(genresText).performClick()
        assertTrue(didNavigate)
    }

    @Test
    fun playlistItemClicked_navigatesToTrackList() {
        var didNavigate = false
        composeTestRule.setContent {
            TestLibraryScreen(
                state = newLibraryState(),
                navigateToPlaylistList = { didNavigate = true })
        }
        composeTestRule.onNodeWithText(playlistsText).performClick()
        assertTrue(didNavigate)
    }

    private fun assertScreenContent() {
        composeTestRule.onNodeWithContentDescription(searchText).assertIsDisplayed()
        composeTestRule.onNodeWithText(libraryText).assertIsDisplayed()
        composeTestRule.onNodeWithText(scanText, useUnmergedTree = true).assertIsDisplayed()

        composeTestRule.onNode(hasText(allSongsText) and hasText(allSongsCountText))
            .assertIsDisplayed()
        composeTestRule.onNode(hasText(artistsText) and hasText(artistsCountText))
            .assertIsDisplayed()
        composeTestRule.onNode(hasText(albumsText) and hasText(albumsCountText))
            .assertIsDisplayed()
        composeTestRule.onNode(hasText(genresText) and hasText(genresCountText))
            .assertIsDisplayed()
        composeTestRule.onNode(hasText(playlistsText) and hasText(playlistsCountText))
            .assertIsDisplayed()
    }

    private fun newLibraryState(
        trackCount: Int = 0,
        artistCount: Int = 0,
        albumCount: Int = 0,
        genreCount: Int = 0,
        playlistCount: Int = 0,
    ): LibraryState {

        composeTestRule.activity.apply {
            allSongsCountText =
                resources.getQuantityString(R.plurals.number_of_tracks, trackCount, trackCount)
            artistsCountText =
                resources.getQuantityString(R.plurals.number_of_artists, artistCount, artistCount)
            albumsCountText =
                resources.getQuantityString(R.plurals.number_of_albums, albumCount, albumCount)
            genresCountText =
                resources.getQuantityString(R.plurals.number_of_genres, genreCount, genreCount)
            playlistsCountText =
                resources.getQuantityString(
                    R.plurals.number_of_playlists,
                    playlistCount,
                    playlistCount
                )
        }

        return LibraryState(
            tracksItem = LibraryItem.Tracks(count = trackCount),
            artistsItem = LibraryItem.Artists(count = artistCount),
            albumsItem = LibraryItem.Albums(count = albumCount),
            genresItem = LibraryItem.Genres(count = genreCount),
            playlistsItem = LibraryItem.Playlists(count = playlistCount)
        )
    }

    @Suppress("TestFunctionName")
    @Composable
    private fun TestLibraryScreen(
        state: LibraryState,
        navigateToSearchScreen: () -> Unit = {},
        navigateToAllTracksList: () -> Unit = {},
        navigateToArtistList: () -> Unit = {},
        navigateToAlbumList: () -> Unit = {},
        navigateToGenreList: () -> Unit = {},
        navigateToPlaylistList: () -> Unit = {},
    ) {
        LibraryScreen(
            state = state,
            navigateToSearchScreen = navigateToSearchScreen,
            navigateToAllTracksList = navigateToAllTracksList,
            navigateToArtistList = navigateToArtistList,
            navigateToAlbumList = navigateToAlbumList,
            navigateToGenreList = navigateToGenreList,
            navigateToPlaylistList = navigateToPlaylistList,
        )
    }

}