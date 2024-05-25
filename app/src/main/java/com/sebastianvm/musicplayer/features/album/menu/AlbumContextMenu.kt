package com.sebastianvm.musicplayer.features.album.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.Screen
import com.sebastianvm.musicplayer.ui.ContextMenu
import com.sebastianvm.musicplayer.ui.MenuItem
import com.sebastianvm.musicplayer.ui.icons.Album
import com.sebastianvm.musicplayer.ui.icons.Artist
import com.sebastianvm.musicplayer.ui.icons.Icons
import com.sebastianvm.musicplayer.ui.icons.PlayArrow
import com.sebastianvm.musicplayer.ui.icons.QueueAdd
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState

data class AlbumContextMenu(
    override val arguments: AlbumContextMenuArguments,
    val navController: NavController
) : Screen<AlbumContextMenuArguments> {

    @Composable
    override fun Content(modifier: Modifier) {
        AlbumContextMenu(
            stateHolder = rememberAlbumContextMenuStateHolder(
                arguments,
                navController
            ),
            modifier = modifier
        )
    }
}

@Composable
fun AlbumContextMenu(
    stateHolder: AlbumContextMenuStateHolder,
    modifier: Modifier = Modifier
) {
    val state by stateHolder.currentState
    AlbumContextMenu(state = state, handle = stateHolder::handle, modifier = modifier)
}

@Composable
private fun AlbumContextMenu(
    state: AlbumContextMenuState,
    handle: Handler<AlbumContextMenuUserAction>,
    modifier: Modifier = Modifier
) {
    when (state) {
        is AlbumContextMenuState.Data -> {
            ContextMenu(menuTitle = state.albumName, modifier = modifier) {
                LazyColumn {
                    item {
                        MenuItem(
                            text = stringResource(id = R.string.play_from_beginning),
                            icon = Icons.PlayArrow.icon(),
                            onItemClicked = {
                                handle(AlbumContextMenuUserAction.PlayAlbumClicked)
                            }
                        )
                    }

                    item {
                        MenuItem(
                            text = stringResource(id = R.string.add_to_queue),
                            icon = Icons.QueueAdd.icon(),
                            onItemClicked = {
                                handle(AlbumContextMenuUserAction.AddToQueueClicked)
                            }
                        )
                    }

                    when (state.viewArtistsState) {
                        is ViewArtistRow.MultipleArtists -> {
                            item {
                                MenuItem(
                                    text = stringResource(id = R.string.view_artists),
                                    icon = Icons.Artist.icon(),
                                    onItemClicked = {
                                        handle(AlbumContextMenuUserAction.ViewArtistsClicked)
                                    }
                                )
                            }
                        }

                        is ViewArtistRow.NoArtists -> Unit
                        is ViewArtistRow.SingleArtist -> {
                            item {
                                MenuItem(
                                    text = stringResource(id = R.string.view_artist),
                                    icon = Icons.Artist.icon(),
                                    onItemClicked = {
                                        handle(
                                            AlbumContextMenuUserAction.ViewArtistClicked(
                                                state.viewArtistsState.artistId
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    item {
                        MenuItem(
                            text = stringResource(id = R.string.view_album),
                            icon = Icons.Album.icon(),
                            onItemClicked = {
                                handle(AlbumContextMenuUserAction.ViewAlbumClicked)
                            }
                        )
                    }
                }
            }
        }

        is AlbumContextMenuState.Loading -> {
            ContextMenu(menuTitle = stringResource(id = R.string.loading), modifier = modifier) {}
        }
    }
}
