package com.sebastianvm.musicplayer.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.EmptyScreen
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.destinations.AlbumContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.ArtistContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.ArtistRouteDestination
import com.sebastianvm.musicplayer.ui.destinations.GenreContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.PlaylistContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.SortBottomSheetDestination
import com.sebastianvm.musicplayer.ui.destinations.TrackContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.ui.library.albumlist.AlbumListLayout
import com.sebastianvm.musicplayer.ui.library.albumlist.AlbumListViewModel
import com.sebastianvm.musicplayer.ui.library.artistlist.ArtistListLayout
import com.sebastianvm.musicplayer.ui.library.artistlist.ArtistListUserAction
import com.sebastianvm.musicplayer.ui.library.artistlist.ArtistListViewModel
import com.sebastianvm.musicplayer.ui.library.genrelist.GenreListLayout
import com.sebastianvm.musicplayer.ui.library.genrelist.GenreListUserAction
import com.sebastianvm.musicplayer.ui.library.genrelist.GenreListViewModel
import com.sebastianvm.musicplayer.ui.library.playlistlist.PlaylistListLayout
import com.sebastianvm.musicplayer.ui.library.playlistlist.PlaylistListViewModel
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListLayout
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListUserAction
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListViewModel
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegateImpl
import com.sebastianvm.musicplayer.ui.search.SearchScreen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import kotlinx.coroutines.launch

enum class TopLevelScreen(@StringRes val screenName: Int) {
    ALL_SONGS(R.string.all_songs),
    ARTISTS(R.string.artists),
    ALBUMS(R.string.albums),
    GENRES(R.string.genres),
    PLAYLISTS(R.string.playlists)
}

@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    navigator: DestinationsNavigator
) {
    MainScreenLayout(
        searchScreen = {
            SearchScreen(
                screenViewModel = hiltViewModel(),
                navigationDelegate = NavigationDelegateImpl(navigator)
            )
        }
    ) { page ->
        Screens(page = page, navigator = navigator)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreenLayout(
    searchScreen: @Composable () -> Unit,
    content: @Composable (page: TopLevelScreen) -> Unit
) {
    val pages = TopLevelScreen.values()
    val pagerState = rememberPagerState {
        pages.size
    }
    val currentScreen: TopLevelScreen by remember {
        derivedStateOf {
            pages[pagerState.currentPage]
        }
    }

    val coroutineScope = rememberCoroutineScope()
    Box {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
}

@Composable
fun Screens(page: TopLevelScreen, navigator: DestinationsNavigator) {
    when (page) {
        TopLevelScreen.ALL_SONGS -> {
            val vm = hiltViewModel<TrackListViewModel>()
            val uiState by vm.stateFlow.collectAsStateWithLifecycle()
            UiStateScreen(
                uiState = uiState,
                modifier = Modifier.fillMaxSize(),
                emptyScreen = {
                    StoragePermissionNeededEmptyScreen(
                        message = R.string.no_tracks_found,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    )
                }
            ) { state ->
                TrackListLayout(
                    state = state,
                    onTrackClicked = { trackIndex ->
                        vm.handle(TrackListUserAction.TrackClicked(trackIndex = trackIndex))
                    },
                    openSortMenu = { args ->
                        navigator.navigate(SortBottomSheetDestination(args))
                    },
                    onDismissPlaybackErrorDialog = {
                        vm.handle(TrackListUserAction.DismissPlaybackErrorDialog)
                    },
                    openTrackContextMenu = { args ->
                        navigator.navigate(TrackContextMenuDestination(args))
                    },
                    modifier = Modifier
                )
            }
        }

        TopLevelScreen.ARTISTS -> {
            val vm = hiltViewModel<ArtistListViewModel>()
            val uiState by vm.stateFlow.collectAsStateWithLifecycle()
            UiStateScreen(
                uiState = uiState,
                modifier = Modifier.fillMaxSize(),
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
                    changeSort = { vm.handle(ArtistListUserAction.SortByButtonClicked) },
                    modifier = Modifier
                )
            }
        }

        TopLevelScreen.ALBUMS -> {
            val vm = hiltViewModel<AlbumListViewModel>()
            val uiState by vm.stateFlow.collectAsStateWithLifecycle()
            UiStateScreen(
                uiState = uiState,
                modifier = Modifier.fillMaxSize(),
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
                    openAlbumContextMenu = { args ->
                        navigator.navigate(AlbumContextMenuDestination(args))
                    }
                )
            }
        }

        TopLevelScreen.GENRES -> {
            val vm = hiltViewModel<GenreListViewModel>()
            val uiState by vm.stateFlow.collectAsStateWithLifecycle()
            UiStateScreen(
                uiState = uiState,
                modifier = Modifier.fillMaxSize(),
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
                    changeSort = { vm.handle(GenreListUserAction.SortByButtonClicked) },
                    openGenreContextMenu = { args ->
                        navigator.navigate(GenreContextMenuDestination(args))
                    }
                )
            }
        }

        TopLevelScreen.PLAYLISTS -> {
            val vm = hiltViewModel<PlaylistListViewModel>()
            val uiState by vm.stateFlow.collectAsStateWithLifecycle()
            UiStateScreen(
                uiState = uiState,
                modifier = Modifier.fillMaxSize(),
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

@ScreenPreview
@Composable
fun MainScreenPreview() {
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
