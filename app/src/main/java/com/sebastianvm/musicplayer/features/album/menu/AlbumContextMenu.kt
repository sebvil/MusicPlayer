package com.sebastianvm.musicplayer.features.album.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.designsystem.icons.Artist
import com.sebastianvm.musicplayer.designsystem.icons.Icons
import com.sebastianvm.musicplayer.designsystem.icons.PlayArrow
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.ui.ContextMenu
import com.sebastianvm.musicplayer.ui.MenuItem
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler

data class AlbumContextMenu(
    override val arguments: AlbumContextMenuArguments,
    val navController: NavController,
) :
    BaseUiComponent<
        AlbumContextMenuArguments,
        AlbumContextMenuState,
        AlbumContextMenuUserAction,
        AlbumContextMenuStateHolder,
    >() {

    override fun createStateHolder(dependencies: Dependencies): AlbumContextMenuStateHolder {
        return AlbumContextMenuStateHolder(
            arguments = arguments,
            albumRepository = dependencies.repositoryProvider.albumRepository,
            playbackManager = dependencies.repositoryProvider.playbackManager,
            navController = navController,
        )
    }

    @Composable
    override fun Content(
        state: AlbumContextMenuState,
        handle: Handler<AlbumContextMenuUserAction>,
        modifier: Modifier,
    ) {
        AlbumContextMenu(state = state, handle = handle, modifier = modifier)
    }
}

@Composable
private fun AlbumContextMenu(
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
                            text = stringResource(id = R.string.play_from_beginning),
                            icon = Icons.PlayArrow.icon(),
                            onItemClicked = { handle(AlbumContextMenuUserAction.PlayAlbumClicked) },
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
                                    },
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
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
        is AlbumContextMenuState.Loading -> {
            ContextMenu(menuTitle = stringResource(id = R.string.loading), modifier = modifier) {}
        }
    }
}
