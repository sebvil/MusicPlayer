package com.sebastianvm.musicplayer.features.album.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.core.designsystems.components.MenuItem
import com.sebastianvm.musicplayer.core.designsystems.icons.AppIcons
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.components.ContextMenu
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.kspannotations.MvvmComponent

@MvvmComponent(vmClass = AlbumContextMenuViewModel::class)
@Composable
fun AlbumContextMenu(
    state: AlbumContextMenuState,
    handle: Handler<AlbumContextMenuUserAction>,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is AlbumContextMenuState.Data -> {
            ContextMenu(menuTitle = state.albumName, modifier = modifier) {
                LazyColumn {
                    item {
                        MenuItem(
                            text = stringResource(id = RString.play_from_beginning),
                            icon = AppIcons.PlayArrow.icon(),
                            onItemClick = { handle(AlbumContextMenuUserAction.PlayAlbumClicked) },
                        )
                    }

                    when (state.viewArtistsState) {
                        is ViewArtistRow.MultipleArtists -> {
                            item {
                                MenuItem(
                                    text = stringResource(id = RString.view_artists),
                                    icon = AppIcons.Artist.icon(),
                                    onItemClick = {
                                        handle(AlbumContextMenuUserAction.ViewArtistsClicked)
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
                                            AlbumContextMenuUserAction.ViewArtistClicked(
                                                state.viewArtistsState.artistId
                                            )
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
        is AlbumContextMenuState.Loading -> {
            ContextMenu(menuTitle = stringResource(id = RString.loading), modifier = modifier) {}
        }
    }
}
