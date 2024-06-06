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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.di.AppDependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.NoArguments
import kotlinx.coroutines.launch

data class HomeUiComponent(val navController: NavController) :
    BaseUiComponent<NoArguments, HomeState, HomeUserAction, HomeStateHolder>() {
    override val arguments: NoArguments = NoArguments

    override fun createStateHolder(dependencies: AppDependencies): HomeStateHolder {
        return getHomeStateHolder(navController)
    }

    @Composable
    override fun Content(state: HomeState, handle: Handler<HomeUserAction>, modifier: Modifier) {
        HomeScreen(state = state, modifier = modifier)
    }
}

@Composable
fun HomeScreen(state: HomeState, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        state.searchUiComponent.Content(modifier = Modifier)
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
                    state.trackListUiComponent.Content(
                        modifier = Modifier.consumeWindowInsets(WindowInsets.systemBars)
                    )
                }
                TopLevelScreen.ARTISTS -> {
                    state.artistListUiComponent.Content(modifier = Modifier)
                }
                TopLevelScreen.ALBUMS -> {
                    state.albumListUiComponent.Content(modifier = Modifier)
                }
                TopLevelScreen.GENRES -> {
                    state.genreListUiComponent.Content(modifier = Modifier)
                }
                TopLevelScreen.PLAYLISTS -> {
                    state.playlistListUiComponent.Content(modifier = Modifier)
                }
            }
        }
    }
}

private enum class TopLevelScreen(@StringRes val screenName: Int) {
    ALL_SONGS(R.string.all_songs),
    ARTISTS(R.string.artists),
    ALBUMS(R.string.albums),
    GENRES(R.string.genres),
    PLAYLISTS(R.string.playlists)
}
