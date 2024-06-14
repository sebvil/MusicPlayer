package com.sebastianvm.musicplayer.features.track.menu

import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.designsystem.icons.Album
import com.sebastianvm.musicplayer.designsystem.icons.Artist
import com.sebastianvm.musicplayer.designsystem.icons.Icons
import com.sebastianvm.musicplayer.designsystem.icons.PlaylistRemove
import com.sebastianvm.musicplayer.designsystem.icons.QueueAdd
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.ui.ContextMenu
import com.sebastianvm.musicplayer.ui.MenuItem
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.util.resources.RString

data class TrackContextMenu(
    override val arguments: TrackContextMenuArguments,
    val navController: NavController,
) :
    BaseUiComponent<
        TrackContextMenuArguments,
        TrackContextMenuState,
        TrackContextMenuUserAction,
        TrackContextMenuStateHolder,
    >() {

    override fun createStateHolder(dependencies: Dependencies): TrackContextMenuStateHolder {
        return TrackContextMenuStateHolder(
            arguments = arguments,
            trackRepository = dependencies.repositoryProvider.trackRepository,
            playlistRepository = dependencies.repositoryProvider.playlistRepository,
            queueRepository = dependencies.repositoryProvider.queueRepository,
            navController = navController,
        )
    }

    @Composable
    override fun Content(
        state: TrackContextMenuState,
        handle: Handler<TrackContextMenuUserAction>,
        modifier: Modifier,
    ) {
        TrackContextMenu(state = state, handle = handle, modifier = modifier)
    }
}

@Composable
private fun TrackContextMenu(
    state: TrackContextMenuState,
    handle: Handler<TrackContextMenuUserAction>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val addedToQueue = stringResource(RString.added_to_queue)
    when (state) {
        is TrackContextMenuState.Data -> {
            ContextMenu(menuTitle = state.trackName, modifier = modifier) {
                LazyColumn {
                    item {
                        MenuItem(
                            text = stringResource(id = RString.add_to_queue),
                            icon = Icons.QueueAdd.icon(),
                            onItemClick = {
                                handle(TrackContextMenuUserAction.AddToQueueClicked)
                                Toast.makeText(
                                        /* context = */ context,
                                        /* text = */ addedToQueue,
                                        /* duration = */ Toast.LENGTH_LONG,
                                    )
                                    .show()
                            },
                        )
                    }

                    item {
                        MenuItem(
                            text = stringResource(id = RString.add_to_playlist),
                            icon = Icons.QueueAdd.icon(),
                            onItemClick = { TODO() },
                        )
                    }

                    when (state.viewArtistsState) {
                        is ViewArtistRow.MultipleArtists -> {
                            item {
                                MenuItem(
                                    text = stringResource(id = RString.view_artists),
                                    icon = Icons.Artist.icon(),
                                    onItemClick = {
                                        handle(TrackContextMenuUserAction.ViewArtistsClicked)
                                    },
                                )
                            }
                        }
                        is ViewArtistRow.NoArtists -> Unit
                        is ViewArtistRow.SingleArtist -> {
                            item {
                                MenuItem(
                                    text = stringResource(id = RString.view_artist),
                                    icon = Icons.Artist.icon(),
                                    onItemClick = {
                                        handle(
                                            TrackContextMenuUserAction.ViewArtistClicked(
                                                state.viewArtistsState.artistId
                                            )
                                        )
                                    },
                                )
                            }
                        }
                    }

                    state.viewAlbumState?.let {
                        item {
                            MenuItem(
                                text = stringResource(id = RString.view_album),
                                icon = Icons.Album.icon(),
                                onItemClick = {
                                    handle(TrackContextMenuUserAction.ViewAlbumClicked(it.albumId))
                                },
                            )
                        }
                    }

                    state.removeFromPlaylistRow?.let {
                        item {
                            MenuItem(
                                text = stringResource(id = RString.remove_from_playlist),
                                icon = Icons.PlaylistRemove.icon(),
                                onItemClick = {
                                    handle(
                                        TrackContextMenuUserAction.RemoveFromPlaylistClicked(
                                            it.playlistId,
                                            it.trackPositionInPlaylist,
                                        )
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
        is TrackContextMenuState.Loading -> {
            ContextMenu(menuTitle = stringResource(id = RString.loading), modifier = modifier) {}
        }
    }
}
