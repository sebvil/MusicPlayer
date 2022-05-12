package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ComposePreviews
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

interface GenreListScreenNavigationDelegate {
    fun navigateUp()
    fun navigateToGenre(genreId: Long)
    fun openContextMenu(genreId: Long)
}

@Composable
fun GenreListScreen(
    screenViewModel: GenreListViewModel = viewModel(),
    delegate: GenreListScreenNavigationDelegate,
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is GenreListUiEvent.NavigateToGenre -> {
                    delegate.navigateToGenre(event.genreId)
                }
                is GenreListUiEvent.NavigateUp -> delegate.navigateUp()
                is GenreListUiEvent.OpenContextMenu -> {
                    delegate.openContextMenu(event.genreId)
                }
            }
        },
        topBar = {
            LibraryTopBar(
                title = stringResource(id = R.string.genres),
                delegate = object : LibraryTopBarDelegate {
                    override fun sortByClicked() {
                        screenViewModel.onSortByClicked()
                    }

                    override fun upButtonClicked() {
                        screenViewModel.onUpButtonClicked()
                    }
                })
        }) { state ->
        GenreListLayout(state = state, object : GenreListScreenDelegate {
            override fun onGenreClicked(genreId: Long) {
                screenViewModel.onGenreClicked(genreId)
            }

            override fun onContextMenuIconClicked(genreId: Long) {
                screenViewModel.onGenreOverflowMenuIconClicked(genreId)
            }
        })
    }
}

interface GenreListScreenDelegate {
    fun onGenreClicked(genreId: Long) = Unit
    fun onContextMenuIconClicked(genreId: Long) = Unit
}

@ComposePreviews
@Composable
fun GenreListScreenPreview(
    @PreviewParameter(GenreListStatePreviewParameterProvider::class) state: GenreListState
) {
    ScreenPreview {
        GenreListLayout(state = state, object : GenreListScreenDelegate {
            override fun onGenreClicked(genreId: Long) = Unit
        })
    }
}


@Composable
fun GenreListLayout(
    state: GenreListState,
    delegate: GenreListScreenDelegate
) {
    LazyColumn {
        items(state.genreList) { item ->
            SingleLineListItem(
                modifier = Modifier.clickable { delegate.onGenreClicked(item.id) },
                afterListContent = {
                    IconButton(
                        onClick = { delegate.onContextMenuIconClicked(item.id) },
                        modifier = Modifier.padding(end = AppDimensions.spacing.xSmall)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = stringResource(R.string.more)
                        )
                    }
                }
            ) {
                Text(
                    text = item.genreName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

        }
    }
}
