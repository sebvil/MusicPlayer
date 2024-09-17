package com.sebastianvm.musicplayer.features.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.core.designsystems.components.Text
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.kspannotations.MvvmComponent
import kotlinx.coroutines.launch

@MvvmComponent(vmClass = HomeViewModel::class)
@Composable
fun Home(state: HomeState, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        state.searchMvvmComponent.Content(modifier = Modifier)
        HomeScreenPager(state = state)
    }
}

@Composable
fun HomeScreenPager(state: HomeState, modifier: Modifier = Modifier) {
    val pages = TopLevelScreen.entries
    val pagerState = rememberPagerState { pages.size }
    val currentScreen: TopLevelScreen = pages[pagerState.currentPage]
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        ScrollableTabRow(selectedTabIndex = pages.indexOf(currentScreen)) {
            pages.forEachIndexed { index, page ->
                Tab(
                    selected = currentScreen == page,
                    onClick = { coroutineScope.launch { pagerState.scrollToPage(index) } },
                    text = { Text(text = stringResource(id = page.screenName)) },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1,
        ) { pageIndex ->
            when (pages[pageIndex]) {
                TopLevelScreen.ALL_SONGS -> {
                    state.trackListMvvmComponent.Content(modifier = Modifier)
                }
                TopLevelScreen.ARTISTS -> {
                    state.artistListMvvmComponent.Content(modifier = Modifier)
                }
                TopLevelScreen.ALBUMS -> {
                    state.albumListMvvmComponent.Content(modifier = Modifier)
                }
                TopLevelScreen.GENRES -> {
                    state.genreListMvvmComponent.Content(modifier = Modifier)
                }
                TopLevelScreen.PLAYLISTS -> {
                    state.playlistListMvvmComponent.Content(
                        modifier = Modifier.consumeWindowInsets(WindowInsets.systemBars)
                    )
                }
            }
        }
    }
}

private enum class TopLevelScreen(@StringRes val screenName: Int) {
    ALL_SONGS(RString.all_songs),
    ARTISTS(RString.artists),
    ALBUMS(RString.albums),
    GENRES(RString.genres),
    PLAYLISTS(RString.playlists),
}
