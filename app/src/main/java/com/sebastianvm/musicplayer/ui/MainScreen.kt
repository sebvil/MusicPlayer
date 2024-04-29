package com.sebastianvm.musicplayer.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.features.album.list.AlbumList
import com.sebastianvm.musicplayer.features.album.list.AlbumListStateHolder
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.artist.list.ArtistList
import com.sebastianvm.musicplayer.features.artist.list.ArtistListStateHolder
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.genre.list.GenreList
import com.sebastianvm.musicplayer.features.genre.list.GenreListStateHolder
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.playlist.list.PlaylistList
import com.sebastianvm.musicplayer.features.playlist.list.PlaylistListStateHolder
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.track.list.TrackList
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.features.track.list.TrackListStateHolder
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.search.SearchScreen
import com.sebastianvm.musicplayer.ui.util.compose.PreviewScreens
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolder
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
    handlePlayback: PlaybackHandler
) {
    MainScreenLayout(searchScreen = {
        SearchScreen(navigator = navigator)
    }) { page ->
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
                Tab(selected = currentScreen == page, onClick = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(index)
                    }
                }, text = { Text(text = stringResource(id = page.screenName)) })
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
    artistListStateHolder: ArtistListStateHolder = stateHolder { dependencyContainer ->
        ArtistListStateHolder(
            artistRepository = dependencyContainer.repositoryProvider.artistRepository,
            sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository,
            artistContextMenuStateHolderFactory = ArtistContextMenuStateHolderFactory(
                dependencyContainer = dependencyContainer,
                navigator = navigator
            )
        )
    },
    albumListStateHolder: AlbumListStateHolder = stateHolder { dependencyContainer ->
        AlbumListStateHolder(
            albumRepository = dependencyContainer.repositoryProvider.albumRepository,
            sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository,
            albumContextMenuStateHolderFactory = AlbumContextMenuStateHolderFactory(
                dependencyContainer = dependencyContainer,
                navigator = navigator
            )
        )
    },
    genreListStateHolder: GenreListStateHolder = stateHolder { dependencyContainer ->
        GenreListStateHolder(
            genreRepository = dependencyContainer.repositoryProvider.genreRepository,
            sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository,
            genreContextMenuStateHolderFactory = GenreContextMenuStateHolderFactory(
                dependencyContainer = dependencyContainer,
                navigator = navigator
            )
        )
    },
    trackListStateHolder: TrackListStateHolder = stateHolder { dependencyContainer ->
        TrackListStateHolder(
            args = TrackListArguments(trackListType = MediaGroup.AllTracks),
            trackRepository = dependencyContainer.repositoryProvider.trackRepository,
            sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository,
            trackContextMenuStateHolderFactory = TrackContextMenuStateHolderFactory(
                dependencyContainer = dependencyContainer,
                navigator = navigator
            )
        )
    },
    playlistListStateHolder: PlaylistListStateHolder = stateHolder { dependencyContainer ->
        PlaylistListStateHolder(
            playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
            sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository,
            playlistContextMenuStateHolderFactory = PlaylistContextMenuStateHolderFactory(
                dependencyContainer = dependencyContainer,
                navigator = navigator
            )
        )
    }
) {
    when (page) {
        TopLevelScreen.ALL_SONGS -> {
            TrackList(
                stateHolder = trackListStateHolder,
                navigator = navigator,
                modifier = modifier.fillMaxSize(),
            )
        }

        TopLevelScreen.ARTISTS -> {
            ArtistList(
                stateHolder = artistListStateHolder,
                navigator = navigator,
                modifier = modifier.fillMaxSize()
            )
        }

        TopLevelScreen.ALBUMS -> {
            AlbumList(
                stateHolder = albumListStateHolder,
                navigator = navigator,
                modifier = modifier.fillMaxSize()
            )
        }

        TopLevelScreen.GENRES -> {
            GenreList(
                stateHolder = genreListStateHolder,
                navigator = navigator,
                modifier.fillMaxSize()
            )
        }

        TopLevelScreen.PLAYLISTS -> {
            PlaylistList(
                stateHolder = playlistListStateHolder,
                navigator = navigator,
                modifier = modifier.fillMaxSize()
            )
        }
    }
}

@PreviewScreens
@Composable
private fun MainScreenPreview() {
    ScreenPreview {
        MainScreenLayout(searchScreen = {}) { _ ->
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
    ALL_SONGS(
        R.string.all_songs
    ),
    ARTISTS(R.string.artists), ALBUMS(R.string.albums), GENRES(R.string.genres), PLAYLISTS(
        R.string.playlists
    )
}
