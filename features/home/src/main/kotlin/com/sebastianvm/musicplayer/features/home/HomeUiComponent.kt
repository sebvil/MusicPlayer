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
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.core.ui.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import kotlinx.coroutines.launch

class HomeUiComponent(private val navController: NavController) :
    BaseUiComponent<HomeState, HomeUserAction, HomeStateHolder>() {

    override fun createStateHolder(services: Services): HomeStateHolder {
        return HomeStateHolder(features = services.featureRegistry, navController = navController)
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
    ALL_SONGS(RString.all_songs),
    ARTISTS(RString.artists),
    ALBUMS(RString.albums),
    GENRES(RString.genres),
    PLAYLISTS(RString.playlists)
}
