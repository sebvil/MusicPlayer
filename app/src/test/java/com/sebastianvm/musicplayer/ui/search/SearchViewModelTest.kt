package com.sebastianvm.musicplayer.ui.search

import com.sebastianvm.musicplayer.database.entities.Fixtures
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.fts.SearchMode
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.BaseTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest : BaseTest() {

    private lateinit var ftsRepository: FullTextSearchRepository
    private lateinit var playbackManager: PlaybackManager

    private fun generateViewModel(
        searchMode: SearchMode = SearchMode.SONGS,
        playbackResult: PlaybackResult? = null
    ): SearchViewModel {
        return SearchViewModel(
            initialState = SearchState(
                selectedOption = searchMode,
                searchResults = listOf(),
                playbackResult = playbackResult
            ),
            ftsRepository = ftsRepository,
            playbackManager = playbackManager,
            defaultDispatcher = Dispatchers.Main
        )
    }

    private suspend fun SearchViewModel.handleWithDelay(action: SearchUserAction) {
        handle(action)
        delay(SearchViewModel.DEBOUNCE_TIME + 1)
    }

    @Before
    fun setUp() {
        ftsRepository = mockk()
        playbackManager = mockk()
    }

    @Test
    fun `TextChanged changes results when searching for songs`() = testScope.runReliableTest {
        every { ftsRepository.searchTracks("a") } returns flowOf(listOf(Fixtures.basicTrackArgentina))
        every { ftsRepository.searchTracks("") } returns flowOf(listOf())
        with(generateViewModel()) {
            assertEquals(listOf(), state.searchResults)
            handleWithDelay(SearchUserAction.TextChanged("a"))
            assertEquals(
                listOf(Fixtures.trackArgentina.toModelListItemState()),
                state.searchResults
            )
        }
    }

    @Test
    fun `TextChanged changes results when searching for artists`() = testScope.runReliableTest {
        every { ftsRepository.searchArtists("a") } returns flowOf(listOf(Fixtures.artistAna))
        every { ftsRepository.searchArtists("") } returns flowOf(listOf())
        with(generateViewModel(searchMode = SearchMode.ARTISTS)) {
            assertEquals(listOf(), state.searchResults)
            handleWithDelay(SearchUserAction.TextChanged("a"))
            assertEquals(
                listOf(Fixtures.artistAna.toModelListItemState()),
                state.searchResults
            )
        }
    }

    @Test
    fun `TextChanged changes results when searching for albums`() = testScope.runReliableTest {
        every { ftsRepository.searchAlbums("a") } returns flowOf(listOf(Fixtures.albumAlpaca))
        every { ftsRepository.searchAlbums("") } returns flowOf(listOf())
        with(generateViewModel(searchMode = SearchMode.ALBUMS)) {
            assertEquals(listOf(), state.searchResults)
            handleWithDelay(SearchUserAction.TextChanged("a"))
            assertEquals(
                listOf(Fixtures.albumAlpaca.toModelListItemState()),
                state.searchResults
            )
        }
    }

    @Test
    fun `TextChanged changes results when searching for genres`() = testScope.runReliableTest {
        every { ftsRepository.searchGenres("a") } returns flowOf(listOf(Fixtures.genreAlpha))
        every { ftsRepository.searchGenres("") } returns flowOf(listOf())
        with(generateViewModel(searchMode = SearchMode.GENRES)) {
            assertEquals(listOf(), state.searchResults)
            handleWithDelay(SearchUserAction.TextChanged("a"))
            assertEquals(
                listOf(Fixtures.genreAlpha.toModelListItemState()),
                state.searchResults
            )
        }
    }

    @Test
    fun `TextChanged changes results when searching for playlists`() = testScope.runReliableTest {
        every { ftsRepository.searchPlaylists("a") } returns flowOf(listOf(Fixtures.playlistApple))
        every { ftsRepository.searchPlaylists("") } returns flowOf(listOf())
        with(generateViewModel(searchMode = SearchMode.PLAYLISTS)) {
            assertEquals(listOf(), state.searchResults)
            handleWithDelay(SearchUserAction.TextChanged("a"))
            assertEquals(
                listOf(Fixtures.playlistApple.toModelListItemState()),
                state.searchResults
            )
        }
    }

    @Test
    fun `SearchModeChanged updates selectedOptions and changes results`() =
        testScope.runReliableTest {
            every { ftsRepository.searchTracks(any()) } returns flowOf(listOf(Fixtures.basicTrackArgentina))
            every { ftsRepository.searchArtists(any()) } returns flowOf(listOf(Fixtures.artistAna))
            every { ftsRepository.searchAlbums(any()) } returns flowOf(listOf(Fixtures.albumAlpaca))
            every { ftsRepository.searchGenres(any()) } returns flowOf(listOf(Fixtures.genreAlpha))
            every { ftsRepository.searchPlaylists(any()) } returns flowOf(listOf(Fixtures.playlistApple))

            with(generateViewModel()) {
                delay(SearchViewModel.DEBOUNCE_TIME + 1)
                assertEquals(
                    listOf(Fixtures.trackArgentina.toModelListItemState()),
                    state.searchResults
                )

                handleWithDelay(SearchUserAction.SearchModeChanged(newMode = SearchMode.ARTISTS))
                assertEquals(SearchMode.ARTISTS, state.selectedOption)
                assertEquals(
                    listOf(Fixtures.artistAna.toModelListItemState()),
                    state.searchResults
                )

                handleWithDelay(SearchUserAction.SearchModeChanged(newMode = SearchMode.ALBUMS))
                assertEquals(SearchMode.ALBUMS, state.selectedOption)
                assertEquals(
                    listOf(Fixtures.albumAlpaca.toModelListItemState()),
                    state.searchResults
                )

                handleWithDelay(SearchUserAction.SearchModeChanged(newMode = SearchMode.GENRES))
                assertEquals(SearchMode.GENRES, state.selectedOption)
                assertEquals(
                    listOf(Fixtures.genreAlpha.toModelListItemState()),
                    state.searchResults
                )

                handleWithDelay(SearchUserAction.SearchModeChanged(newMode = SearchMode.PLAYLISTS))
                assertEquals(SearchMode.PLAYLISTS, state.selectedOption)
                assertEquals(
                    listOf(Fixtures.playlistApple.toModelListItemState()),
                    state.searchResults
                )

                handleWithDelay(SearchUserAction.SearchModeChanged(newMode = SearchMode.SONGS))
                assertEquals(SearchMode.SONGS, state.selectedOption)
                assertEquals(
                    listOf(Fixtures.trackArgentina.toModelListItemState()),
                    state.searchResults
                )
            }
        }

    @Test
    fun `SearchResultClicked triggers playback and on success navigates to player when searching for tracks`() =
        testScope.runReliableTest {
            every { ftsRepository.searchTracks(any()) } returns flowOf(listOf())
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every { playbackManager.playSingleTrack(0) } returns result
            with(generateViewModel()) {
                handle(SearchUserAction.SearchResultClicked(id = 0))
                assertEquals(PlaybackResult.Loading, state.playbackResult)
                result.value = PlaybackResult.Success
                assertNull(state.playbackResult)
                assertEquals(
                    listOf(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer)),
                    navEvents.value
                )
            }
        }

    @Test
    fun `SearchResultClicked triggers playback and on failure sets playback result when searching for tracks`() =
        testScope.runReliableTest {
            every { ftsRepository.searchTracks(any()) } returns flowOf(listOf())
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every { playbackManager.playSingleTrack(0) } returns result
            with(generateViewModel()) {
                handle(SearchUserAction.SearchResultClicked(id = 0))
                assertEquals(PlaybackResult.Loading, state.playbackResult)
                result.value = PlaybackResult.Error(0)
                assertEquals(PlaybackResult.Error(0), state.playbackResult)
                assertEquals(listOf(), navEvents.value)
            }
        }

    @Test
    fun `SearchResultClicked navigates to artist screen when searching for artists`() {
        with(generateViewModel(searchMode = SearchMode.ARTISTS)) {
            handle(SearchUserAction.SearchResultClicked(id = 0))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.Artist(
                            ArtistArguments(artistId = 0)
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    @Test
    fun `SearchResultClicked navigates to album screen when searching for albums`() {
        with(generateViewModel(searchMode = SearchMode.ALBUMS)) {
            handle(SearchUserAction.SearchResultClicked(id = 0))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackList(
                            TrackListArgumentsForNav(
                                trackListType = TrackList.ALBUM,
                                trackListId = 0
                            )
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    @Test
    fun `SearchResultClicked navigates to genre screen when searching for genres`() {
        with(generateViewModel(searchMode = SearchMode.GENRES)) {
            handle(SearchUserAction.SearchResultClicked(id = 0))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackList(
                            TrackListArgumentsForNav(
                                trackListType = TrackList.GENRE,
                                trackListId = 0
                            )
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    @Test
    fun `SearchResultClicked navigates to playlist screen when searching for playlists`() {
        with(generateViewModel(searchMode = SearchMode.PLAYLISTS)) {
            handle(SearchUserAction.SearchResultClicked(id = 0))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackList(
                            TrackListArgumentsForNav(
                                trackListType = TrackList.PLAYLIST,
                                trackListId = 0
                            )
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    @Test
    fun `SearchResultOverflowMenuIconClicked navigates to track context menu when searching for songs`() {
        with(generateViewModel()) {
            handle(SearchUserAction.SearchResultOverflowMenuIconClicked(id = 0))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            TrackContextMenuArguments(
                                trackId = 0,
                                trackList = com.sebastianvm.musicplayer.player.MediaGroup(
                                    mediaId = 0,
                                    mediaGroupType = MediaGroupType.SINGLE_TRACK
                                )
                            )
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    @Test
    fun `SearchResultOverflowMenuIconClicked navigates to artist context menu when searching for artists`() {
        with(generateViewModel(searchMode = SearchMode.ARTISTS)) {
            handle(SearchUserAction.SearchResultOverflowMenuIconClicked(id = 0))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.ArtistContextMenu(
                            ArtistContextMenuArguments(artistId = 0)
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    @Test
    fun `SearchResultOverflowMenuIconClicked navigates to album context menu when searching for albums`() {
        with(generateViewModel(searchMode = SearchMode.ALBUMS)) {
            handle(SearchUserAction.SearchResultOverflowMenuIconClicked(id = 0))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.AlbumContextMenu(
                            AlbumContextMenuArguments(albumId = 0)
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    @Test
    fun `SearchResultOverflowMenuIconClicked navigates to genre context menu when searching for artists`() {
        with(generateViewModel(searchMode = SearchMode.GENRES)) {
            handle(SearchUserAction.SearchResultOverflowMenuIconClicked(id = 0))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.GenreContextMenu(
                            GenreContextMenuArguments(genreId = 0)
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    @Test
    fun `SearchResultOverflowMenuIconClicked navigates to playlist context menu when searching for artists`() {
        with(generateViewModel(searchMode = SearchMode.PLAYLISTS)) {
            handle(SearchUserAction.SearchResultOverflowMenuIconClicked(id = 0))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.PlaylistContextMenu(
                            PlaylistContextMenuArguments(playlistId = 0)
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    @Test
    fun `UpButtonClicked adds NavigateUp NavEvent`() {
        with(generateViewModel()) {
            handle(SearchUserAction.UpButtonClicked)
            assertEquals(listOf(NavEvent.NavigateUp), navEvents.value)
        }
    }

    @Test
    fun `DismissPlaybackErrorDialog resets playback status state`() {
        with(generateViewModel(playbackResult = PlaybackResult.Error(0))) {
            handle(SearchUserAction.DismissPlaybackErrorDialog)
            assertNull(state.playbackResult)
        }
    }
}
