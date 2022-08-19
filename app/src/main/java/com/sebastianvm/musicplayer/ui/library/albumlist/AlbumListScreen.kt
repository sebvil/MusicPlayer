package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface

@Composable
fun AlbumListScreen(
    screenViewModel: AlbumListViewModel = viewModel(),
    navigationDelegate: NavigationDelegate,
) {
    val listState = rememberLazyListState()
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is AlbumListUiEvent.ScrollToTop -> {
                    listState.scrollToItem(0)
                }
            }
        },
        navigationDelegate = navigationDelegate,
        topBar = {
            LibraryTopBar(
                title = stringResource(id = R.string.albums),
                delegate = object : LibraryTopBarDelegate {
                    override fun upButtonClicked() {
                        screenViewModel.handle(AlbumListUserAction.UpButtonClicked)
                    }

                    override fun sortByClicked() {
                        screenViewModel.handle(AlbumListUserAction.SortByClicked)
                    }
                }
            )
        }) {
        AlbumListLayout(viewModel = screenViewModel, listState = listState)
    }
}

@ScreenPreview
@Composable
fun AlbumListScreenPreview(@PreviewParameter(AlbumListStatePreviewParameterProvider::class) state: AlbumListState) {
    val lazyListState = rememberLazyListState()
    ScreenPreview(state) { vm ->
        AlbumListLayout(
            viewModel = vm,
            listState = lazyListState
        )
    }
}

@Composable
fun AlbumListLayout(
    viewModel: ViewModelInterface<AlbumListState, AlbumListUserAction>,
    listState: LazyListState
) {
    val state by viewModel.state.collectAsState()
    LazyColumn(state = listState) {
        items(state.albumList) { item ->
            ModelListItem(
                state = item,
                modifier = Modifier.clickable {
                    viewModel.handle(AlbumListUserAction.AlbumClicked(item.id))
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            viewModel.handle(
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
