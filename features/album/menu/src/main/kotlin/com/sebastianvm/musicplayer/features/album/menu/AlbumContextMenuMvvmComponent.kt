package com.sebastianvm.musicplayer.features.album.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.designsystems.components.MenuItem
import com.sebastianvm.musicplayer.core.designsystems.icons.AppIcons
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.components.ContextMenu
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseMvvmComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class AlbumContextMenuMvvmComponent(
    private val arguments: AlbumContextMenuArguments,
    private val navController: NavController,
    private val albumRepository: AlbumRepository,
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) :
    BaseMvvmComponent<
        AlbumContextMenuState,
        AlbumContextMenuUserAction,
        AlbumContextMenuViewModel,
    >() {

    override val viewModel: AlbumContextMenuViewModel by lazy {
        AlbumContextMenuViewModel(
            arguments = arguments,
            albumRepository = albumRepository,
            playbackManager = playbackManager,
            navController = navController,
            features = features,
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
