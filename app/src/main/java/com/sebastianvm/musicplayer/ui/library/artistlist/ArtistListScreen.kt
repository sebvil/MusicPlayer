package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface

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
                        screenViewModel.handle(ArtistListUserAction.SortByButtonClicked)
                    }

                    override fun upButtonClicked() {
                        screenViewModel.handle(ArtistListUserAction.UpButtonClicked)
                    }
                })
        }
    ) {
        ArtistListLayout(screenViewModel)
    }
}

@ScreenPreview
@Composable
fun ArtistListScreenPreview(@PreviewParameter(ArtistListStatePreviewParameterProvider::class) state: ArtistListState) {
    ScreenPreview(state) { vm ->
        ArtistListLayout(viewModel = vm)
    }
}

@Composable
fun ArtistListLayout(viewModel: ViewModelInterface<ArtistListState, ArtistListUserAction>) {
    val state by viewModel.state.collectAsState()
    LazyColumn {
        items(state.artistList) { item ->
            ModelListItem(
                state = item,
                modifier = Modifier.clickable {
                    viewModel.handle(ArtistListUserAction.ArtistRowClicked(item.id))
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            viewModel.handle(ArtistListUserAction.ArtistOverflowMenuIconClicked(item.id))
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = com.sebastianvm.commons.R.drawable.ic_overflow),
                            contentDescription = stringResource(id = com.sebastianvm.commons.R.string.more)
                        )
                    }
                }
            )
        }
    }
}
