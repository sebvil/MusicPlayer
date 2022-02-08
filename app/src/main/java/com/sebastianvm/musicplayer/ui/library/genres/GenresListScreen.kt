package com.sebastianvm.musicplayer.ui.library.genres

import android.content.res.Configuration
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

interface GenresListScreenNavigationDelegate {
    fun navigateUp()
    fun navigateToGenre(genreName: String)
    fun openContextMenu(genreName: String, currentSort: MediaSortOption, sortOrder: MediaSortOrder)
}

@Composable
fun GenresListScreen(
    screenViewModel: GenresListViewModel = viewModel(),
    delegate: GenresListScreenNavigationDelegate,
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is GenresListUiEvent.NavigateToGenre -> {
                    delegate.navigateToGenre(event.genreName)
                }
                is GenresListUiEvent.NavigateUp -> delegate.navigateUp()
                is GenresListUiEvent.OpenContextMenu -> {
                    delegate.openContextMenu(event.genreName, event.currentSort, event.sortOrder)
                }
            }
        },
        topBar = {
            LibraryTopBar(
                title = stringResource(id = R.string.genres),
                delegate = object : LibraryTopBarDelegate {
                    override fun sortByClicked() {
                        screenViewModel.handle(GenresListUserAction.SortByClicked)
                    }

                    override fun upButtonClicked() {
                        screenViewModel.handle(GenresListUserAction.UpButtonClicked)
                    }
                })
        }) { state ->
        GenresListLayout(state = state, object : GenresListScreenDelegate {
            override fun onGenreClicked(genreName: String) {
                screenViewModel.handle(action = GenresListUserAction.GenreClicked(genreName = genreName))
            }

            override fun onContextMenuIconClicked(genreName: String) {
                screenViewModel.handle(
                    action = GenresListUserAction.OverflowMenuIconClicked(
                        genreName = genreName
                    )
                )
            }
        })
    }
}

interface GenresListScreenDelegate {
    fun onGenreClicked(genreName: String) = Unit
    fun onContextMenuIconClicked(genreName: String) = Unit
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GenresListScreenPreview(
    @PreviewParameter(GenresListStatePreviewParameterProvider::class) state: GenresListState
) {
    ScreenPreview {
        GenresListLayout(state = state, object : GenresListScreenDelegate {
            override fun onGenreClicked(genreName: String) = Unit
        })
    }
}


@Composable
fun GenresListLayout(
    state: GenresListState,
    delegate: GenresListScreenDelegate
) {
    LazyColumn {
        items(state.genresList) { item ->
            SingleLineListItem(
                modifier = Modifier.clickable { delegate.onGenreClicked(item.genreName) },
                afterListContent = {
                    IconButton(
                        onClick = { delegate.onContextMenuIconClicked(genreName = item.genreName) },
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
