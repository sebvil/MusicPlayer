package com.sebastianvm.musicplayer.ui.playlist

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.util.compose.ComposePreviews
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

interface PlaylistScreenNavigationDelegate {
    fun navigateUp()
}

@Composable
fun PlaylistScreen(screenViewModel: PlaylistViewModel, delegate: PlaylistScreenNavigationDelegate) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
        topBar = {
            LibraryTopBar(title = it.playlistName,
                delegate = object : LibraryTopBarDelegate {
                    override fun upButtonClicked() {
                        delegate.navigateUp()
                    }
                })
        },
        fab = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.add_tracks)) },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus),
                        contentDescription = "Plus"
                    )
                },
                onClick = { /*TODO*/ })
        }) { state ->
        PlaylistLayout(state = state)
    }
}


@ComposePreviews
@Composable
fun PlaylistScreenPreview(@PreviewParameter(PlaylistStatePreviewParameterProvider::class) state: PlaylistState) {
    ScreenPreview(topBar = {
        LibraryTopBar(title = state.playlistName, delegate = object : LibraryTopBarDelegate {})
    }, fab = {
        ExtendedFloatingActionButton(
            text = { Text(text = stringResource(R.string.add_tracks)) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = "Plus"
                )
            },
            onClick = { /*TODO*/ })
    }) {
        PlaylistLayout(state = state)
    }
}

@Composable
fun PlaylistLayout(state: PlaylistState) {
    if (state.trackList.isEmpty()) {
        Text(stringResource(R.string.playlist_without_tracks))
    }
}
