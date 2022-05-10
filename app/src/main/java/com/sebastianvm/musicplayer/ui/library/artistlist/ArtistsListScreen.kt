package com.sebastianvm.musicplayer.ui.library.artistlist

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.ArtistRow
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

interface ArtistsListScreenNavigationDelegate {
    fun navigateUp()
    fun navigateToArtist(artistId: Long)
    fun openContextMenu(artistId: Long)
}

@Composable
fun ArtistsListScreen(
    screenViewModel: ArtistListViewModel = viewModel(),
    delegate: ArtistsListScreenNavigationDelegate
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is ArtistListUiEvent.NavigateToArtist -> {
                    delegate.navigateToArtist(event.artistId)
                }
                is ArtistListUiEvent.NavigateUp -> delegate.navigateUp()
                is ArtistListUiEvent.OpenContextMenu -> delegate.openContextMenu(event.artistId)
            }
        },
        topBar = {
            LibraryTopBar(
                title = stringResource(id = R.string.artists),
                delegate = object : LibraryTopBarDelegate {
                    override fun sortByClicked() {
                        screenViewModel.onSortByClicked()
                    }

                    override fun upButtonClicked() {
                        screenViewModel.onUpButtonClicked()
                    }
                })
        }
    ) { state ->
        ArtistsListLayout(state = state, delegate = object : ArtistsListScreenDelegate {
            override fun onArtistRowClicked(artistId: Long) {
                screenViewModel.onArtistClicked(artistId = artistId)
            }

            override fun onArtistOverflowMenuIconClicked(artistId: Long) {
                screenViewModel.onArtistOverflowMenuIconClicked(artistId)
            }
        })
    }
}

interface ArtistsListScreenDelegate {
    fun onArtistRowClicked(artistId: Long) = Unit
    fun onArtistOverflowMenuIconClicked(artistId: Long) = Unit
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ArtistsListScreenPreview(@PreviewParameter(ArtistsListStatePreviewParameterProvider::class) state: ArtistListState) {
    ScreenPreview {
        ArtistsListLayout(state = state, delegate = object : ArtistsListScreenDelegate {})
    }
}

@Composable
fun ArtistsListLayout(
    state: ArtistListState,
    delegate: ArtistsListScreenDelegate
) {
    LazyColumn {
        items(state.artistsList) { item ->
            ArtistRow(
                state = item,
                modifier = Modifier.clickable {
                    delegate.onArtistRowClicked(item.artistId)
                },
                onOverflowMenuIconClicked = { delegate.onArtistOverflowMenuIconClicked(item.artistId) }
            )
        }
    }
}
