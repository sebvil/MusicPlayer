package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.ArtistRow
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun ArtistListScreen(
    screenViewModel: ArtistListViewModel = viewModel(),
    navigationDelegate: NavigationDelegate,
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
        navigationDelegate = navigationDelegate,
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
        ArtistListLayout(state = state, delegate = object : ArtistListScreenDelegate {
            override fun onArtistRowClicked(artistId: Long) {
                screenViewModel.onArtistClicked(artistId = artistId)
            }

            override fun onArtistOverflowMenuIconClicked(artistId: Long) {
                screenViewModel.onArtistOverflowMenuIconClicked(artistId)
            }
        })
    }
}

interface ArtistListScreenDelegate {
    fun onArtistRowClicked(artistId: Long) = Unit
    fun onArtistOverflowMenuIconClicked(artistId: Long) = Unit
}

@ScreenPreview
@Composable
fun ArtistListScreenPreview(@PreviewParameter(ArtistListStatePreviewParameterProvider::class) state: ArtistListState) {
    ScreenPreview {
        ArtistListLayout(state = state, delegate = object : ArtistListScreenDelegate {})
    }
}

@Composable
fun ArtistListLayout(
    state: ArtistListState,
    delegate: ArtistListScreenDelegate
) {
    LazyColumn {
        items(state.artistList) { item ->
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
