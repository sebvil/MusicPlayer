package com.sebastianvm.musicplayer.features.track.menu

import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.core.designsystems.components.MenuItem
import com.sebastianvm.musicplayer.core.designsystems.icons.AppIcons
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.components.ContextMenu
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.kspannotations.MvvmComponent

@MvvmComponent(vmClass = TrackContextMenuViewModel::class)
@Composable
fun TrackContextMenu(
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
                            icon = AppIcons.QueueAdd.icon(),
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
                            icon = AppIcons.QueueAdd.icon(),
                            onItemClick = { TODO() },
                        )
                    }

                    when (state.viewArtistsState) {
                        is ViewArtistRow.MultipleArtists -> {
                            item {
                                MenuItem(
                                    text = stringResource(id = RString.view_artists),
                                    icon = AppIcons.Artist.icon(),
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
                                    icon = AppIcons.Artist.icon(),
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
                                icon = AppIcons.Album.icon(),
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
                                icon = AppIcons.PlaylistRemove.icon(),
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
