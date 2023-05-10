package com.sebastianvm.musicplayer.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.library.albumlist.AlbumListLayout
import com.sebastianvm.musicplayer.ui.library.albumlist.AlbumListViewModel
import com.sebastianvm.musicplayer.ui.library.artistlist.ArtistListLayout
import com.sebastianvm.musicplayer.ui.library.artistlist.ArtistListViewModel
import com.sebastianvm.musicplayer.ui.library.genrelist.GenreListLayout
import com.sebastianvm.musicplayer.ui.library.genrelist.GenreListViewModel
import com.sebastianvm.musicplayer.ui.library.playlistlist.PlaylistListLayout
import com.sebastianvm.musicplayer.ui.library.playlistlist.PlaylistListViewModel
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListLayout
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListUserAction
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListViewModel
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import kotlinx.coroutines.launch

enum class TopLevelScreen(
    @StringRes val screenName: Int,
    val navigationDestination: NavigationDestination
) {
    ALL_SONGS(
        R.string.all_songs,
        NavigationDestination.TrackList(TrackListArguments(trackList = MediaGroup.AllTracks))
    ),
    ARTISTS(R.string.artists, NavigationDestination.ArtistsRoot),
    ALBUMS(R.string.albums, NavigationDestination.AlbumsRoot),
    GENRES(R.string.genres, NavigationDestination.GenresRoot),
    PLAYLISTS(R.string.playlists, NavigationDestination.PlaylistsRoot)
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    paddingValues: PaddingValues,
    content: @Composable (page: TopLevelScreen, paddingValues: PaddingValues) -> Unit
) {
    val pages = TopLevelScreen.values()
    val pagerState = rememberPagerState(initialPage = 0)
    val currentScreen: TopLevelScreen by remember {
        derivedStateOf {
            pages[pagerState.currentPage]
        }
    }

    var isSearchActive by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()
    Box {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SearchBar(
                query = "",
                onQueryChange = {},
                onSearch = {},
                active = isSearchActive,
                onActiveChange = { isSearchActive = it },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "")
                },
                trailingIcon = {
                    if (!isSearchActive) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(id = R.string.more)
                        )
                    }

                },
                placeholder = {
                    Text(text = stringResource(R.string.search_media))
                }
            ) {
            }

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
                content(pages[pageIndex], paddingValues)
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
                onDismissPlaybackErrorDialog = {
                    vm.handle(TrackListUserAction.DismissPlaybackErrorDialog)
                },
                openTrackContextMenu = {},
                modifier = Modifier,
                updateAlpha = {}
            )
        }

        TopLevelScreen.ARTISTS -> {
            val vm = hiltViewModel<ArtistListViewModel>()
            val state by vm.stateFlow.collectAsStateWithLifecycle()
            ArtistListLayout(
                state = state,
                openArtistContextMenu = {},
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
                openGenreContextMenu = {}
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
                openPlaylistContextMenu = {}
            )
        }
    }
}


@ScreenPreview
@Composable
fun MainScreenPreview() {
    ScreenPreview {
        MainScreen(paddingValues = PaddingValues(bottom = 20.dp)) { _, padding ->
            LazyColumn(contentPadding = padding) {
                items(count = 20, key = { it }) {
                    ListItem(headlineContent = {
                        Text(text = it.toString())
                    })
                }
            }
        }
    }
}