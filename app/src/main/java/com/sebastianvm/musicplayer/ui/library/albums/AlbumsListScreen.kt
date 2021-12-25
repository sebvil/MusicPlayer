package com.sebastianvm.musicplayer.ui.library.albums

import android.content.res.Configuration
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.AlbumRow
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.lists.ListItemDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.util.SortOrder

interface AlbumsListScreenNavigationDelegate {
    fun navigateToAlbum(albumId: String)
    fun navigateUp()
    fun openSortMenu(sortOption: Int, sortOrder: SortOrder)
    fun openContextMenu(albumId: String)
}


@Composable
fun AlbumsListScreen(
    screenViewModel: AlbumsListViewModel = viewModel(),
    delegate: AlbumsListScreenNavigationDelegate,
) {
    val listState = rememberLazyListState()
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is AlbumsListUiEvent.NavigateToAlbum -> {
                    delegate.navigateToAlbum(event.albumId)
                }
                is AlbumsListUiEvent.NavigateUp -> {
                    delegate.navigateUp()
                }
                is AlbumsListUiEvent.ShowSortBottomSheet -> {
                    delegate.openSortMenu(event.sortOption, event.sortOrder)
                }
                is AlbumsListUiEvent.ScrollToTop -> {
                    listState.scrollToItem(0)
                }
                is AlbumsListUiEvent.OpenContextMenu -> {
                    delegate.openContextMenu(event.albumId)
                }
            }
        },
        topBar = {
            LibraryTopBar(
                title = DisplayableString.ResourceValue(R.string.albums),
                delegate = object : LibraryTopBarDelegate {
                    override fun upButtonClicked() {
                        screenViewModel.handle(AlbumsListUserAction.UpButtonClicked)
                    }

                    override fun sortByClicked() {
                        screenViewModel.handle(AlbumsListUserAction.SortByClicked)
                    }
                }
            )
        }) { state ->
        AlbumsListLayout(state = state, listState = listState, object : AlbumsListScreenDelegate {
            override fun onAlbumClicked(albumId: String) {
                screenViewModel.handle(AlbumsListUserAction.AlbumClicked(albumId))
            }

            override fun onAlbumContextButtonClicked(albumId: String) {
                screenViewModel.handle(AlbumsListUserAction.AlbumContextButtonClicked(albumId = albumId))
            }
        })
    }
}

interface AlbumsListScreenDelegate {
    fun onAlbumClicked(albumId: String) = Unit
    fun onAlbumContextButtonClicked(albumId: String) = Unit
}


@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlbumsListScreenPreview(@PreviewParameter(AlbumsListStatePreviewParameterProvider::class) state: AlbumsListState) {
    val lazyListState = rememberLazyListState()
    ScreenPreview {
        AlbumsListLayout(state = state, listState = lazyListState, object : AlbumsListScreenDelegate {})
    }
}

@Composable
fun AlbumsListLayout(
    state: AlbumsListState,
    listState: LazyListState,
    delegate: AlbumsListScreenDelegate
) {
    LazyColumn(state = listState) {
        items(state.albumsList) { item ->
            AlbumRow(
                state = item.albumRowState,
                delegate = object : ListItemDelegate {
                    override fun onItemClicked() {
                        delegate.onAlbumClicked(item.albumId)
                    }

                    override fun onSecondaryActionIconClicked() {
                        delegate.onAlbumContextButtonClicked(item.albumId)
                    }
                }
            )
        }
    }
}





