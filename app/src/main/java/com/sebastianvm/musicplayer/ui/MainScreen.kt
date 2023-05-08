package com.sebastianvm.musicplayer.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListViewModel
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import kotlinx.coroutines.launch

enum class TopLevelScreen(@StringRes val screenName: Int) {
    ALL_SONGS(R.string.all_songs),
    ARTISTS(R.string.artists),
    ALBUMS(R.string.albums),
    GENRES(R.string.genres),
    PLAYLISTS(R.string.playlists)
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(content: @Composable (page: TopLevelScreen, paddingBottom: Dp) -> Unit) {
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

    var height by remember {
        mutableStateOf(0f)
    }
    val density = LocalDensity.current
    val playerBottomPadding = 16.dp

    val paddingDp by remember {
        derivedStateOf {
            with(density) {
                playerBottomPadding + height.toDp() + 8.dp
            }
        }
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
                content(pages[pageIndex], paddingDp)
            }
        }
        PlayerCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 8.dp)
                .padding(bottom = playerBottomPadding)
                .onPlaced {
                    height = it.boundsInParent().height
                }
        )

    }


}


@Composable
fun PlayerCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_album),
                contentDescription = "",
                modifier = Modifier.fillMaxHeight(),
                contentScale = ContentScale.FillHeight
            )
            Column(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, top = 8.dp)) {
                Text(
                    text = "Track name",
                    modifier = Modifier.padding(bottom = 2.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "Artist name", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            androidx.compose.material3.IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_prev),
                    contentDescription = stringResource(R.string.previous),
                )
            }
            androidx.compose.material3.IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = stringResource(R.string.previous),
                )
            }
            androidx.compose.material3.IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_next),
                    contentDescription = stringResource(R.string.previous),
                )
            }
        }
        LinearProgressIndicator(
            progress = 0.5f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            trackColor = MaterialTheme.colorScheme.onPrimary

        )
    }
}


@ComponentPreview
@Composable
fun PlayerCardPreview() {
    ThemedPreview {
        PlayerCard()
    }
}

@Composable
fun Screens(page: TopLevelScreen) {
    when (page) {
        TopLevelScreen.ALL_SONGS -> {
            val vm = hiltViewModel<TrackListViewModel>()
            val state = vm.stateFlow.collectAsStateWithLifecycle()
            TrackListLayout(
                state = state.value,
                onTrackClicked = {},
                openTrackContextMenu = {},
                onDismissPlaybackErrorDialog = { /*TODO*/ },
                updateAlpha = {}
            )
        }

        TopLevelScreen.ARTISTS -> {
            val vm = hiltViewModel<ArtistListViewModel>()
            val state = vm.stateFlow.collectAsStateWithLifecycle()
            ArtistListLayout(
                state = state.value,
                openArtistContextMenu = {},
                navigateToArtistScreen = {}
            )
        }

        TopLevelScreen.ALBUMS -> {
            val vm = hiltViewModel<AlbumListViewModel>()
            val state = vm.stateFlow.collectAsStateWithLifecycle()
            AlbumListLayout(
                state = state.value,
                navigateToAlbum = {},
                openAlbumContextMenu = {}
            )
        }

        TopLevelScreen.GENRES -> {
            val vm = hiltViewModel<GenreListViewModel>()
            val state = vm.stateFlow.collectAsStateWithLifecycle()
            GenreListLayout(
                state = state.value,
                navigateToGenre = {},
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
                navigateToPlaylist = {},
                openPlaylistContextMenu = {}
            )
        }
    }
}


@ScreenPreview
@Composable
fun MainScreenPreview() {
    ScreenPreview {
        MainScreen { _, padding ->
            LazyColumn(contentPadding = PaddingValues(bottom = padding)) {
                items(count = 20, key = { it }) {
                    ListItem(headlineContent = {
                        Text(text = it.toString())
                    })
                }
            }
        }
    }
}