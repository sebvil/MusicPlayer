package com.sebastianvm.musicplayer.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.components.EmptyScreen
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.destinations.AlbumContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.ArtistContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.ArtistRouteDestination
import com.sebastianvm.musicplayer.ui.destinations.GenreContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.PlaylistContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.SortBottomSheetDestination
import com.sebastianvm.musicplayer.ui.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.ui.library.albumlist.AlbumListLayout
import com.sebastianvm.musicplayer.ui.library.albumlist.AlbumListState
import com.sebastianvm.musicplayer.ui.library.albumlist.AlbumListStateHolder
import com.sebastianvm.musicplayer.ui.library.albumlist.AlbumListUserAction
import com.sebastianvm.musicplayer.ui.library.artistlist.ArtistListLayout
import com.sebastianvm.musicplayer.ui.library.artistlist.ArtistListState
import com.sebastianvm.musicplayer.ui.library.artistlist.ArtistListStateHolder
import com.sebastianvm.musicplayer.ui.library.artistlist.ArtistListUserAction
import com.sebastianvm.musicplayer.ui.library.genrelist.GenreListLayout
import com.sebastianvm.musicplayer.ui.library.genrelist.GenreListState
import com.sebastianvm.musicplayer.ui.library.genrelist.GenreListStateHolder
import com.sebastianvm.musicplayer.ui.library.genrelist.GenreListUserAction
import com.sebastianvm.musicplayer.ui.library.playlistlist.PlaylistListLayout
import com.sebastianvm.musicplayer.ui.library.playlistlist.PlaylistListState
import com.sebastianvm.musicplayer.ui.library.playlistlist.PlaylistListStateHolder
import com.sebastianvm.musicplayer.ui.library.playlistlist.PlaylistListUserAction
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListRoute
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListState
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListStateHolder
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListUserAction
import com.sebastianvm.musicplayer.ui.search.SearchScreen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreviews
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolder
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
    handlePlayback: PlaybackHandler
) {
    MainScreenLayout(
        searchScreen = {
            SearchScreen(navigator = navigator)
        }
    ) { page ->
        Screens(
            page = page,
            navigator = navigator,
            playMedia = { mediaGroup: MediaGroup, initialTrackIndex: Int ->
                handlePlayback(mediaGroup, initialTrackIndex)
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreenLayout(
    searchScreen: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (page: TopLevelScreen) -> Unit
) {
    val pages = TopLevelScreen.entries
    val pagerState = rememberPagerState {
        pages.size
    }
    val currentScreen: TopLevelScreen by remember {
        derivedStateOf {
            pages[pagerState.currentPage]
        }
    }

    val coroutineScope = rememberCoroutineScope()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.windowInsetsPadding(WindowInsets.statusBars)
    ) {
        searchScreen()
        ScrollableTabRow(selectedTabIndex = pages.indexOf(currentScreen)) {
            pages.forEachIndexed { index, page ->
                Tab(
                    selected = currentScreen == page,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(index)
                        }
                    },
                    text = { Text(text = stringResource(id = page.screenName)) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondBoundsPageCount = 1
        ) { pageIndex ->
            content(pages[pageIndex])
        }
    }
}

@Composable
fun Screens(
    page: TopLevelScreen,
    navigator: DestinationsNavigator,
    playMedia: (mediaGroup: MediaGroup, initialTrackIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    artistListStateHolder: StateHolder<UiState<ArtistListState>, ArtistListUserAction> =
        stateHolder { dependencyContainer ->
            ArtistListStateHolder(
                artistRepository = dependencyContainer.repositoryProvider.artistRepository,
                sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository
            )
        },
    albumListStateHolder: StateHolder<UiState<AlbumListState>, AlbumListUserAction> =
        stateHolder { dependencyContainer ->
            AlbumListStateHolder(
                albumRepository = dependencyContainer.repositoryProvider.albumRepository,
                sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository
            )
        },
    genreListStateHolder: StateHolder<UiState<GenreListState>, GenreListUserAction> =
        stateHolder { dependencyContainer ->
            GenreListStateHolder(
                genreRepository = dependencyContainer.repositoryProvider.genreRepository,
                sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository
            )
        },
    trackListStateHolder: StateHolder<UiState<TrackListState>, TrackListUserAction> =
        stateHolder { dependencyContainer ->

            TrackListStateHolder(
                args = TrackListArguments(trackListType = MediaGroup.AllTracks),
                trackRepository = dependencyContainer.repositoryProvider.trackRepository,
                sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository
            )
        },
    playlistListStateHolder: StateHolder<UiState<PlaylistListState>, PlaylistListUserAction> =
        stateHolder { dependencyContainer ->
            PlaylistListStateHolder(
                playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
                sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository,
            )
        }
) {
    when (page) {
        TopLevelScreen.ALL_SONGS -> {
            TrackListRoute(
                navigator = navigator,
                handlePlayback = playMedia,
                modifier = modifier.fillMaxSize(),
                trackListStateHolder = trackListStateHolder
            )
        }

        TopLevelScreen.ARTISTS -> {
            val uiState by artistListStateHolder.state.collectAsStateWithLifecycle()
            UiStateScreen(
                uiState = uiState,
                modifier = modifier.fillMaxSize(),
                emptyScreen = {
                    StoragePermissionNeededEmptyScreen(
                        message = R.string.no_artists_found,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    )
                }
            ) { state ->
                ArtistListLayout(
                    state = state,
                    openArtistContextMenu = { args ->
                        navigator.navigate(ArtistContextMenuDestination(args))
                    },
                    navigateToArtistScreen = { args ->
                        navigator.navigate(ArtistRouteDestination(args))
                    },
                    changeSort = { artistListStateHolder.handle(ArtistListUserAction.SortByButtonClicked) },
                    modifier = Modifier
                )
            }
        }

        TopLevelScreen.ALBUMS -> {
            val uiState by albumListStateHolder.state.collectAsStateWithLifecycle()
            UiStateScreen(
                uiState = uiState,
                modifier = modifier.fillMaxSize(),
                emptyScreen = {
                    StoragePermissionNeededEmptyScreen(
                        message = R.string.no_albums_found,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    )
                }
            ) { state ->
                AlbumListLayout(
                    state = state,
                    navigateToAlbum = { args ->
                        navigator.navigate(TrackListRouteDestination(args))
                    },
                    openSortMenu = { args ->
                        navigator.navigate(SortBottomSheetDestination(args))
                    },
                    openAlbumContextMenu = { args ->
                        navigator.navigate(AlbumContextMenuDestination(args))
                    }
                )
            }
        }

        TopLevelScreen.GENRES -> {
            val uiState by genreListStateHolder.state.collectAsStateWithLifecycle()
            UiStateScreen(
                uiState = uiState,
                modifier = modifier.fillMaxSize(),
                emptyScreen = {
                    StoragePermissionNeededEmptyScreen(
                        message = R.string.no_genres_found,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    )
                }
            ) { state ->
                GenreListLayout(
                    state = state,
                    navigateToGenre = { args ->
                        navigator.navigate(TrackListRouteDestination(args))
                    },
                    changeSort = { genreListStateHolder.handle(GenreListUserAction.SortByButtonClicked) },
                    openGenreContextMenu = { args ->
                        navigator.navigate(GenreContextMenuDestination(args))
                    }
                )
            }
        }

        TopLevelScreen.PLAYLISTS -> {
            val uiState by playlistListStateHolder.state.collectAsStateWithLifecycle()
            UiStateScreen(
                uiState = uiState,
                modifier = modifier.fillMaxSize(),
                emptyScreen = {
                    EmptyScreen(
                        message = {
                            Text(
                                text = stringResource(R.string.no_playlists_try_creating_one),
                                textAlign = TextAlign.Center
                            )
                        },
                        button = {
                            Button(onClick = {}) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Text(text = stringResource(id = R.string.create_playlist))
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    )
                }
            ) { state ->
                PlaylistListLayout(
                    state = state,
                    isCreatePlaylistDialogOpen = false,
                    onDismissPlaylistCreationDialog = { /*TODO*/ },
                    onDismissPlaylistCreationErrorDialog = { /*TODO*/ },
                    onCreatePlaylistCLicked = {},
                    navigateToPlaylist = { args ->
                        navigator.navigate(TrackListRouteDestination(args))
                    },
                    openPlaylistContextMenu = { args ->
                        navigator.navigate(PlaylistContextMenuDestination(args))
                    }
                )
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun MainScreenPreview() {
    ScreenPreview {
        MainScreenLayout(
            searchScreen = {}
        ) { _ ->
            LazyColumn {
                items(count = 20, key = { it }) {
                    ListItem(headlineContent = {
                        Text(text = it.toString())
                    })
                }
            }
        }
    }
}

enum class TopLevelScreen(@StringRes val screenName: Int) {
    ALL_SONGS(R.string.all_songs),
    ARTISTS(R.string.artists),
    ALBUMS(R.string.albums),
    GENRES(R.string.genres),
    PLAYLISTS(R.string.playlists)
}
