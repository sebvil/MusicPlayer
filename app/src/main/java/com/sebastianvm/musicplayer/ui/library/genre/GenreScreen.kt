package com.sebastianvm.musicplayer.ui.library.genre

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.lists.tracklist.TrackList
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen


@Composable
fun GenreScreen(
    screenViewModel: GenreViewModel,
    navigationDelegate: NavigationDelegate,
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { },
        navigationDelegate = navigationDelegate,
        topBar = { state ->
            LibraryTopBar(
                title = state.genreName,
                delegate = object : LibraryTopBarDelegate {
                    override fun upButtonClicked() {
                        screenViewModel.handle(GenreUserAction.UpButtonClicked)
                    }

                    override fun sortByClicked() {
                        screenViewModel.handle(GenreUserAction.SortByButtonClicked)
                    }
                })
        },
    ) {
        TrackList(viewModel = hiltViewModel(), navigationDelegate = navigationDelegate)
    }
}

