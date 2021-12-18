package com.sebastianvm.musicplayer.ui.library.albums

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.AlbumRow
import com.sebastianvm.musicplayer.ui.components.LibraryTitle
import com.sebastianvm.musicplayer.ui.components.ListWithHeader
import com.sebastianvm.musicplayer.ui.components.ListWithHeaderState
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun AlbumsListScreen(
    screenViewModel: AlbumsListViewModel = viewModel(),
    navigateToAlbum: (albumId: String) -> Unit
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is AlbumsListUiEvent.NavigateToAlbum -> {
                    navigateToAlbum(event.albumGid)
                }
            }
        }) { state ->
        AlbumsListLayout(state = state, object : AlbumsListScreenDelegate {
            override fun onAlbumClicked(albumGid: String, albumName: String) {
                screenViewModel.handle(AlbumsListUserAction.AlbumClicked(albumGid, albumName))
            }
        })
    }
}

interface AlbumsListScreenDelegate {
    fun onAlbumClicked(albumGid: String, albumName: String)
}


@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AlbumsListScreenPreview(@PreviewParameter(AlbumsListStatePreviewParameterProvider::class) state: AlbumsListState) {
    ScreenPreview {
        AlbumsListLayout(state = state, object : AlbumsListScreenDelegate {
            override fun onAlbumClicked(albumGid: String, albumName: String) = Unit
        })
    }
}

@Composable
fun AlbumsListLayout(
    state: AlbumsListState,
    delegate: AlbumsListScreenDelegate
) {
    val listState = ListWithHeaderState(
        DisplayableString.ResourceValue(R.string.albums),
        state.albumsList,
        { header -> LibraryTitle(title = header) },
        { item ->
            AlbumRow(
                state = item.albumRowState,
                modifier = Modifier
                    .clickable {
                        delegate.onAlbumClicked(item.albumGid, item.albumRowState.albumName)
                    },
            )
        }
    )
    ListWithHeader(state = listState)
}





