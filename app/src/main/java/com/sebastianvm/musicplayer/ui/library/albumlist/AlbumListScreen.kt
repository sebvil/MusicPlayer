package com.sebastianvm.musicplayer.ui.library.albumlist

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
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenLayout
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate

@Composable
fun AlbumListScreen(viewModel: AlbumListViewModel, navigationDelegate: NavigationDelegate) {
    val listState = rememberLazyListState()
    Screen(
        screenViewModel = viewModel,
        eventHandler = { event ->
            when (event) {
                is AlbumListUiEvent.ScrollToTop -> {
                    listState.scrollToItem(0)
                }
            }
        },
        navigationDelegate = navigationDelegate
    ) { state, delegate ->
        AlbumListScreen(
            state = state,
            screenDelegate = delegate,
            listState = listState
        )
    }
}

@Composable
fun AlbumListScreen(
    state: AlbumListState,
    screenDelegate: ScreenDelegate<AlbumListUserAction>,
    listState: LazyListState
) {
    ScreenLayout(
        topBar = {
            LibraryTopBar(
                title = stringResource(id = R.string.albums),
                delegate = object : LibraryTopBarDelegate {
                    override fun upButtonClicked() {
                        screenDelegate.handle(AlbumListUserAction.UpButtonClicked)
                    }

                    override fun sortByClicked() {
                        screenDelegate.handle(AlbumListUserAction.SortByClicked)
                    }
                }
            )
        }) {
        AlbumListLayout(state = state, screenDelegate = screenDelegate, listState = listState)
    }
}

@Composable
fun AlbumListLayout(
    state: AlbumListState,
    screenDelegate: ScreenDelegate<AlbumListUserAction>,
    listState: LazyListState
) {
    LazyColumn(state = listState) {
        items(state.albumList) { item ->
            ModelListItem(
                state = item,
                modifier = Modifier.clickable {
                    screenDelegate.handle(AlbumListUserAction.AlbumClicked(item.id))
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            screenDelegate.handle(
                                AlbumListUserAction.AlbumOverflowIconClicked(
                                    item.id
                                )
                            )
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = com.sebastianvm.commons.R.drawable.ic_overflow),
                            contentDescription = stringResource(id = com.sebastianvm.commons.R.string.more)
                        )
                    }
                }
            )
        }
    }
}
