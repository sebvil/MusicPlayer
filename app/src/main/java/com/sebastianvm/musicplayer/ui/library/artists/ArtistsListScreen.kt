package com.sebastianvm.musicplayer.ui.library.artists

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.ArtistRow
import com.sebastianvm.musicplayer.ui.components.LibraryTitle
import com.sebastianvm.musicplayer.ui.components.ListWithHeader
import com.sebastianvm.musicplayer.ui.components.ListWithHeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ListItemDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun ArtistsListScreen(
    screenViewModel: ArtistsListViewModel = viewModel(),
    navigateToArtist: (String) -> Unit
) {

    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is ArtistsListUiEvent.NavigateToArtist -> {
                    navigateToArtist(event.artistGid)
                }
            }

        },
    ) { state ->
        ArtistsListLayout(state = state, delegate = object : ArtistsListScreenDelegate {
            override fun onArtistRowClicked(artistGid: String) {
                screenViewModel.handle(
                    ArtistsListUserAction.ArtistClicked(artistGid = artistGid)
                )
            }
        })
    }
}

interface ArtistsListScreenDelegate {
    fun onArtistRowClicked(artistGid: String) = Unit
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
    val listState = ListWithHeaderState(
        DisplayableString.ResourceValue(R.string.artists),
        state.artistsList,
        { header -> LibraryTitle(title = header) },
        { item ->
            ArtistRow(state = item, delegate = object : ListItemDelegate {
                override fun onItemClicked() {
                    delegate.onArtistRowClicked(item.artistGid)
                }
            })
        }
    )
    ListWithHeader(state = listState)
}


