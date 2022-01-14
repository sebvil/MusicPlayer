package com.sebastianvm.musicplayer.ui.library.artists

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.ArtistRow
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

interface ArtistsListScreenNavigationDelegate {
    fun navigateUp()
    fun navigateToArtist(artistName: String)
    fun openContextMenu(artistName: String)
}

@Composable
fun ArtistsListScreen(
    screenViewModel: ArtistsListViewModel = viewModel(),
    delegate: ArtistsListScreenNavigationDelegate
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is ArtistsListUiEvent.NavigateToArtist -> {
                    delegate.navigateToArtist(event.artistName)
                }
                is ArtistsListUiEvent.NavigateUp -> delegate.navigateUp()
                is ArtistsListUiEvent.OpenContextMenu -> delegate.openContextMenu(event.artistName)
            }
        },
        topBar = {
            LibraryTopBar(
                title = DisplayableString.ResourceValue(R.string.artists),
                delegate = object : LibraryTopBarDelegate {
                    override fun sortByClicked() {
                        screenViewModel.handle(ArtistsListUserAction.SortByClicked)
                    }

                    override fun upButtonClicked() {
                        screenViewModel.handle(ArtistsListUserAction.UpButtonClicked)
                    }
                })
        }
    ) { state ->
        ArtistsListLayout(state = state, delegate = object : ArtistsListScreenDelegate {
            override fun onArtistRowClicked(artistName: String) {
                screenViewModel.handle(
                    ArtistsListUserAction.ArtistClicked(artistName = artistName)
                )
            }

            override fun onContextMenuIconClicked(artistName: String) {
                screenViewModel.handle(ArtistsListUserAction.ContextMenuIconClicked(artistName))
            }
        })
    }
}

interface ArtistsListScreenDelegate {
    fun onArtistRowClicked(artistName: String) = Unit
    fun onContextMenuIconClicked(artistName: String) = Unit
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ArtistsListScreenPreview(@PreviewParameter(ArtistsListStatePreviewParameterProvider::class) state: ArtistsListState) {
    ScreenPreview {
        ArtistsListLayout(state = state, delegate = object : ArtistsListScreenDelegate {})
    }
}

@Composable
fun ArtistsListLayout(
    state: ArtistsListState,
    delegate: ArtistsListScreenDelegate
) {
    LazyColumn {
        items(state.artistsList) { item ->
            ArtistRow(
                state = item,
                modifier = Modifier.clickable {
                    delegate.onArtistRowClicked(item.artistName)
                },
                onOverflowMenuIconClicked = { delegate.onContextMenuIconClicked(item.artistName) }
            )
        }
    }
}
