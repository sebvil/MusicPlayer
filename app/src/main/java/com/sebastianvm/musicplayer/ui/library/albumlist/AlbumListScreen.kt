package com.sebastianvm.musicplayer.ui.library.albumlist

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.AlbumRow
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

interface AlbumListScreenNavigationDelegate {
    fun navigateToAlbum(albumId: Long)
    fun navigateUp()
    fun openSortMenu()
    fun openContextMenu(albumId: Long)
}


@Composable
fun AlbumListScreen(
    screenViewModel: AlbumListViewModel = viewModel(),
    delegate: AlbumListScreenNavigationDelegate,
) {
    val listState = rememberLazyListState()
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is AlbumListUiEvent.NavigateToAlbum -> {
                    delegate.navigateToAlbum(event.albumId)
                }
                is AlbumListUiEvent.NavigateUp -> {
                    delegate.navigateUp()
                }
                is AlbumListUiEvent.ShowSortBottomSheet -> {
                    delegate.openSortMenu()
                }
                is AlbumListUiEvent.ScrollToTop -> {
                    listState.scrollToItem(0)
                }
                is AlbumListUiEvent.OpenContextMenu -> {
                    delegate.openContextMenu(event.albumId)
                }
            }
        },
        topBar = {
            LibraryTopBar(
                title = stringResource(id = R.string.albums),
                delegate = object : LibraryTopBarDelegate {
                    override fun upButtonClicked() {
                        screenViewModel.onUpButtonClicked()
                    }

                    override fun sortByClicked() {
                        screenViewModel.onSortByClicked()
                    }
                }
            )
        }) { state ->
        AlbumListLayout(state = state, listState = listState, object : AlbumListScreenDelegate {
            override fun onAlbumClicked(albumId: Long) {
                screenViewModel.onAlbumClicked(albumId)
            }

            override fun onAlbumOverflowMenuIconClicked(albumId: Long) {
                screenViewModel.onAlbumOverflowMenuIconClicked(albumId = albumId)
            }
        })
    }
}

interface AlbumListScreenDelegate {
    fun onAlbumClicked(albumId: Long) = Unit
    fun onAlbumOverflowMenuIconClicked(albumId: Long) = Unit
}


@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlbumListScreenPreview(@PreviewParameter(AlbumListStatePreviewParameterProvider::class) state: AlbumListState) {
    val lazyListState = rememberLazyListState()
    ScreenPreview {
        AlbumListLayout(
            state = state,
            listState = lazyListState,
            object : AlbumListScreenDelegate {})
    }
}

@Composable
fun AlbumListLayout(
    state: AlbumListState,
    listState: LazyListState,
    delegate: AlbumListScreenDelegate
) {
    LazyColumn(state = listState) {
        items(state.albumList) { item ->
            AlbumRow(
                state = item,
                modifier = Modifier.clickable {
                    delegate.onAlbumClicked(item.albumId)
                },
                onOverflowMenuIconClicked = { delegate.onAlbumOverflowMenuIconClicked(item.albumId) }
            )
        }
    }
}
