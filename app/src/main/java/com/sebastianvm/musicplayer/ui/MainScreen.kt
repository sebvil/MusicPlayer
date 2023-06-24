package com.sebastianvm.musicplayer.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.library.albumlist.AlbumListLayout
import com.sebastianvm.musicplayer.ui.library.albumlist.AlbumListViewModel
import com.sebastianvm.musicplayer.ui.library.artistlist.ArtistListLayout
import com.sebastianvm.musicplayer.ui.library.artistlist.ArtistListViewModel
import com.sebastianvm.musicplayer.ui.library.genrelist.GenreListLayout
import com.sebastianvm.musicplayer.ui.library.genrelist.GenreListViewModel
import com.sebastianvm.musicplayer.ui.library.playlistlist.PlaylistListLayout
import com.sebastianvm.musicplayer.ui.library.playlistlist.PlaylistListViewModel
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListLayout
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListUserAction
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListViewModel
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
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

@Composable
fun MainScreen(
    navigationDelegate: NavigationDelegate,
    content: @Composable (page: TopLevelScreen) -> Unit
) {
    MainScreenLayout(
        searchScreen = {
            SearchScreen(screenViewModel = hiltViewModel(), navigationDelegate = navigationDelegate)
        },
        content = content
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreenLayout(
    searchScreen: @Composable () -> Unit,
    content: @Composable (page: TopLevelScreen) -> Unit
) {
    val pages = TopLevelScreen.values()
    val pagerState = rememberPagerState(initialPage = 0)
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
                        text = { Text(text = stringResource(id = page.screenName)) })
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageCount = pages.size,
                beyondBoundsPageCount = 1
            ) { pageIndex ->
                content(pages[pageIndex])
            }
        }
    }
}


@Composable
fun Screens(page: TopLevelScreen, navigationDelegate: NavigationDelegate) {
    when (page) {
        TopLevelScreen.ALL_SONGS -> {
            val vm = hiltViewModel<TrackListViewModel>()
            val state by vm.stateFlow.collectAsStateWithLifecycle()
            TrackListLayout(
                state = state,
                onTrackClicked = { trackIndex ->
                    vm.handle(TrackListUserAction.TrackClicked(trackIndex = trackIndex))
                },
                openSortMenu = { args ->
                    navigationDelegate.navigateToScreen(
                        NavigationDestination.SortMenu(
                            arguments = args
                        )
                    )
                },
                onDismissPlaybackErrorDialog = {
                    vm.handle(TrackListUserAction.DismissPlaybackErrorDialog)
                },
                openTrackContextMenu = { args ->
                    navigationDelegate.navigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            arguments = args
                        )
                    )
                },
                modifier = Modifier,
                updateAlpha = {}
            )
        }

        TopLevelScreen.ARTISTS -> {
            val vm = hiltViewModel<ArtistListViewModel>()
            val state by vm.stateFlow.collectAsStateWithLifecycle()
            ArtistListLayout(
                state = state,
                openArtistContextMenu = { args ->
                    navigationDelegate.navigateToScreen(
                        NavigationDestination.ArtistContextMenu(
                            arguments = args
                        )
                    )
                },
                navigateToArtistScreen = { args ->
                    navigationDelegate.navigateToScreen(
                        NavigationDestination.Artist(
                            arguments = args
                        )
                    )
                }
            )
        }

        TopLevelScreen.ALBUMS -> {
            val vm = hiltViewModel<AlbumListViewModel>()
            val state by vm.stateFlow.collectAsStateWithLifecycle()
            AlbumListLayout(
                state = state,
                navigateToAlbum = { args ->
                    navigationDelegate.navigateToScreen(
                        NavigationDestination.TrackList(
                            arguments = args
                        )
                    )
                },
                openAlbumContextMenu = { args ->
                    navigationDelegate.navigateToScreen(
                        NavigationDestination.AlbumContextMenu(
                            arguments = args
                        )
                    )
                }
            )
        }

        TopLevelScreen.GENRES -> {
            val vm = hiltViewModel<GenreListViewModel>()
            val state by vm.stateFlow.collectAsStateWithLifecycle()
            GenreListLayout(
                state = state,
                navigateToGenre = { args ->
                    navigationDelegate.navigateToScreen(
                        NavigationDestination.TrackList(
                            args
                        )
                    )
                },
                openGenreContextMenu = { args ->
                    navigationDelegate.navigateToScreen(
                        NavigationDestination.GenreContextMenu(
                            arguments = args
                        )
                    )
                }
            )
        }

        TopLevelScreen.PLAYLISTS -> {
            val vm = hiltViewModel<PlaylistListViewModel>()
            val state = vm.stateFlow.collectAsStateWithLifecycle()
            PlaylistListLayout(
                state = state.value,
                isCreatePlaylistDialogOpen = false,
                onDismissPlaylistCreationDialog = { /*TODO*/ },
                onDismissPlaylistCreationErrorDialog = { /*TODO*/ },
                onCreatePlaylistCLicked = {},
                navigateToPlaylist = { args ->
                    navigationDelegate.navigateToScreen(
                        NavigationDestination.TrackList(
                            arguments = args
                        )
                    )
                },
                openPlaylistContextMenu = { args ->
                    navigationDelegate.navigateToScreen(
                        NavigationDestination.PlaylistContextMenu(
                            arguments = args
                        )
                    )
                }
            )
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