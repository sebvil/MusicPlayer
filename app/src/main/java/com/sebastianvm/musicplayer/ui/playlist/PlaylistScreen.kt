package com.sebastianvm.musicplayer.ui.playlist

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.lists.tracklist.TrackList
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen


@Composable
fun PlaylistScreen(screenViewModel: PlaylistViewModel, navigationDelegate: NavigationDelegate) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { },
        navigationDelegate = navigationDelegate,
        topBar = {
            LibraryTopBar(title = it.playlistName,
                delegate = object : LibraryTopBarDelegate {
                    override fun upButtonClicked() {
                        screenViewModel.onUpButtonClicked()
                    }

                    override fun sortByClicked() {
                        screenViewModel.onSortByClicked()
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
                onClick = { screenViewModel.onAddTracksClicked() })
        }) {
        TrackList(
            viewModel = hiltViewModel(),
            navigationDelegate = navigationDelegate,
        )
    }
}