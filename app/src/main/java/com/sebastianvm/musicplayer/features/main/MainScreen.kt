package com.sebastianvm.musicplayer.features.main

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.features.album.list.AlbumList
import com.sebastianvm.musicplayer.features.artist.list.ArtistList
import com.sebastianvm.musicplayer.features.genre.list.GenreList
import com.sebastianvm.musicplayer.features.navigation.Screen
import com.sebastianvm.musicplayer.features.playlist.list.PlaylistList
import com.sebastianvm.musicplayer.features.search.SearchScreen
import com.sebastianvm.musicplayer.features.track.list.TrackList
import com.sebastianvm.musicplayer.ui.util.mvvm.NoArguments
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState
import kotlinx.coroutines.launch

data object MainScreen : Screen<NoArguments> {
    override val arguments: NoArguments = NoArguments

    @Composable
    override fun Content(modifier: Modifier) {
        val stateHolder = rememberMainStateHolder()
        MainScreen(stateHolder = stateHolder)
    }
}

@Composable
fun MainScreen(stateHolder: MainStateHolder, modifier: Modifier = Modifier) {
    val state by stateHolder.currentState
    MainScreen(state = state, modifier = modifier)
}

@Composable
fun MainScreen(state: MainState, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        SearchScreen(screenStateHolder = state.searchStateHolder)
        MainScreenPager(state = state)

    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreenPager(state: MainState, modifier: Modifier = Modifier) {
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
        modifier = modifier.fillMaxSize(),
        beyondBoundsPageCount = 1
    ) { pageIndex ->
        when (pages[pageIndex]) {
            TopLevelScreen.ALL_SONGS -> {
                TrackList(stateHolder = state.trackListStateHolder)
            }

            TopLevelScreen.ARTISTS -> {
                ArtistList(stateHolder = state.artistListStateHolder)
            }

            TopLevelScreen.ALBUMS -> {
                AlbumList(stateHolder = state.albumListStateHolder)
            }

            TopLevelScreen.GENRES -> {
                GenreList(stateHolder = state.genreListStateHolder)
            }

            TopLevelScreen.PLAYLISTS -> {
                PlaylistList(stateHolder = state.playlistListStateHolder)
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
