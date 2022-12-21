package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.topbar.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenLayout
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate

@Composable
fun ArtistListScreen(viewModel: ArtistListViewModel, navigationDelegate: NavigationDelegate) {
    val listState = rememberLazyListState()
    Screen(
        screenViewModel = viewModel,
        eventHandler = { event ->
            when (event) {
                is ArtistListUiEvent.ScrollToTop -> {
                    listState.scrollToItem(0)
                }
            }
        },
        navigationDelegate = navigationDelegate
    ) { state, delegate ->
        ArtistListScreen(
            state = state,
            screenDelegate = delegate,
            listState = listState
        )
    }
}

@Composable
fun ArtistListScreen(
    state: ArtistListState,
    screenDelegate: ScreenDelegate<ArtistListUserAction>,
    listState: LazyListState
) {
    ScreenLayout(
        topBar = {
            LibraryTopBar(
                title = stringResource(id = R.string.artists),
                delegate = object : LibraryTopBarDelegate {
                    override fun sortByClicked() {
                        screenDelegate.handle(ArtistListUserAction.SortByButtonClicked)
                    }

                    override fun upButtonClicked() {
                        screenDelegate.handle(ArtistListUserAction.UpButtonClicked)
                    }
                })
        }
    ) {
        ArtistListLayout(state = state, screenDelegate = screenDelegate, listState = listState)
    }
}


@Composable
fun ArtistListLayout(
    state: ArtistListState,
    screenDelegate: ScreenDelegate<ArtistListUserAction>,
    listState: LazyListState
) {
    LazyColumn(state = listState) {
        items(state.artistList) { item ->
            ModelListItem(
                state = item,
                modifier = Modifier.clickable {
                    screenDelegate.handle(ArtistListUserAction.ArtistRowClicked(item.id))
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            screenDelegate.handle(
                                ArtistListUserAction.ArtistOverflowMenuIconClicked(
                                    item.id
                                )
                            )
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = stringResource(id = R.string.more)
                        )
                    }
                }
            )
        }
    }
}
