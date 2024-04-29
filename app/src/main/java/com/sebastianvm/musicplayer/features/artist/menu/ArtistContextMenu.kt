package com.sebastianvm.musicplayer.features.artist.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.ContextMenu
import com.sebastianvm.musicplayer.ui.MenuItem
import com.sebastianvm.musicplayer.ui.icons.Artist
import com.sebastianvm.musicplayer.ui.icons.Icons
import com.sebastianvm.musicplayer.ui.icons.PlayArrow
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler

@Composable
fun ArtistContextMenu(
    stateHolder: ArtistContextMenuStateHolder,
    modifier: Modifier = Modifier
) {
    val state by stateHolder.state.collectAsStateWithLifecycle()
    ArtistContextMenu(state = state, handle = stateHolder::handle, modifier = modifier)
}

@Composable
private fun ArtistContextMenu(
    state: ArtistContextMenuState,
    handle: Handler<ArtistContextMenuUserAction>,
    modifier: Modifier = Modifier
) {
    when (state) {
        is ArtistContextMenuState.Data -> {
            ContextMenu(menuTitle = state.artistName, modifier = modifier) {
                LazyColumn {
                    item {
                        MenuItem(
                            text = stringResource(id = R.string.play_all_songs),
                            icon = Icons.PlayArrow.icon(),
                            onItemClicked = {
                                handle(ArtistContextMenuUserAction.PlayArtistClicked)
                            }
                        )
                    }

                    item {
                        MenuItem(
                            text = stringResource(id = R.string.view_artist),
                            icon = Icons.Artist.icon(),
                            onItemClicked = {
                                handle(ArtistContextMenuUserAction.ViewArtistClicked)
                            }
                        )
                    }
                }
            }
        }

        is ArtistContextMenuState.Loading -> {
            ContextMenu(menuTitle = stringResource(id = R.string.loading), modifier = modifier) {}
        }
    }
}
