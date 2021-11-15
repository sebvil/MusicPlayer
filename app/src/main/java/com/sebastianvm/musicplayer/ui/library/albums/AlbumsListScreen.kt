package com.sebastianvm.musicplayer.ui.library.albums

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.AlbumRow
import com.sebastianvm.musicplayer.ui.components.LibraryTitle
import com.sebastianvm.musicplayer.ui.components.ListWithHeader
import com.sebastianvm.musicplayer.ui.components.ListWithHeaderState
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun AlbumsListScreen(
    screenViewModel: AlbumsListViewModel = viewModel(),
    navigateToAlbum: (String, String) -> Unit
) {
    val state = screenViewModel.state.observeAsState(screenViewModel.state.value)
    AlbumsListLayout(state = state.value, object : AlbumsListScreenDelegate {
        override fun onAlbumClicked(albumGid: String, albumName: String) {
            navigateToAlbum(albumGid, albumName)
        }
    })
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
                    }
                    .padding(vertical = 8.dp, horizontal = 32.dp),
            )
        }
    )
    ListWithHeader(state = listState)
}





