package com.sebastianvm.musicplayer.ui.library.playlists

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder

interface PlaylistsListScreenNavigationDelegate {
    fun navigateUp()
    fun navigateToPlaylist(playlistName: String)
    fun openContextMenu(playlistName: String, currentSort: SortOption, sortOrder: SortOrder)
}

@Composable
fun PlaylistsListScreen(
    screenViewModel: PlaylistsListViewModel = viewModel(),
    delegate: PlaylistsListScreenNavigationDelegate,
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is PlaylistsListUiEvent.NavigateToPlaylist -> {
                    delegate.navigateToPlaylist(event.playlistName)
                }
                is PlaylistsListUiEvent.NavigateUp -> delegate.navigateUp()
                is PlaylistsListUiEvent.OpenContextMenu -> {
                    delegate.openContextMenu(event.playlistName, event.currentSort, event.sortOrder)
                }
            }
        },
        topBar = {
            LibraryTopBar(
                title = stringResource(id = R.string.playlists),
                delegate = object : LibraryTopBarDelegate {
                    override fun sortByClicked() {
                        screenViewModel.handle(PlaylistsListUserAction.SortByClicked)
                    }

                    override fun upButtonClicked() {
                        screenViewModel.handle(PlaylistsListUserAction.UpButtonClicked)
                    }
                })
        }) { state ->
        PlaylistsListLayout(state = state, object : PlaylistsListScreenDelegate {
            override fun onPlaylistClicked(playlistName: String) {
                screenViewModel.handle(action = PlaylistsListUserAction.PlaylistClicked(playlistName = playlistName))
            }

            override fun onContextMenuIconClicked(playlistName: String) {
                screenViewModel.handle(
                    action = PlaylistsListUserAction.OverflowMenuIconClicked(
                        playlistName = playlistName
                    )
                )
            }
        })
    }
}

interface PlaylistsListScreenDelegate {
    fun onPlaylistClicked(playlistName: String) = Unit
    fun onContextMenuIconClicked(playlistName: String) = Unit
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PlaylistsListScreenPreview(
    @PreviewParameter(PlaylistsListStatePreviewParameterProvider::class) state: PlaylistsListState
) {
    ScreenPreview {
        PlaylistsListLayout(state = state, object : PlaylistsListScreenDelegate {
            override fun onPlaylistClicked(playlistName: String) = Unit
        })
    }
}


@Composable
fun PlaylistsListLayout(
    state: PlaylistsListState,
    delegate: PlaylistsListScreenDelegate
) {
    LazyColumn {
        items(state.playlistsList) { item ->
            SingleLineListItem(
                modifier = Modifier.clickable { delegate.onPlaylistClicked(item.playlistName) },
                afterListContent = {
                    IconButton(
                        onClick = { delegate.onContextMenuIconClicked(playlistName = item.playlistName) },
                        modifier = Modifier.padding(end = AppDimensions.spacing.xSmall)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = stringResource(R.string.more)
                        )
                    }
                }
            ) {
                Text(
                    text = item.playlistName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

        }
    }
}
