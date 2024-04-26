@file:Suppress("stateHolderForwarding")

package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.PlaybackHandler
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists.ArtistsMenuArguments
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.destinations.ArtistRouteDestination
import com.sebastianvm.musicplayer.ui.destinations.ArtistsBottomSheetDestination
import com.sebastianvm.musicplayer.ui.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolder

@Composable
fun ContextBottomSheet(
    navigator: DestinationsNavigator,
    stateHolder: StateHolder<UiState<ContextMenuState>, BaseContextMenuUserAction>,
    handlePlayback: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by stateHolder.state.collectAsStateWithLifecycle()

    UiStateScreen(
        uiState = uiState,
        emptyScreen = {},
        modifier = modifier
    ) { state ->
        ContextMenuLayout(
            state = state,
            navigator = navigator,
            handlePlayback = {
                handlePlayback()
                navigator.navigateUp()
            },
            screenDelegate = stateHolder::handle
        )
    }
}

@RootNavGraph
@Destination(
    navArgsDelegate = TrackContextMenuArguments::class,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun TrackContextMenu(
    navigator: DestinationsNavigator,
    arguments: TrackContextMenuArguments,
    handlePlayback: PlaybackHandler,
    stateHolder: StateHolder<UiState<ContextMenuState>, BaseContextMenuUserAction> =
        stateHolder { dependencyContainer ->
            TrackContextMenuStateHolder(
                arguments = arguments,
                trackRepository = dependencyContainer.repositoryProvider.trackRepository,
                playbackManager = dependencyContainer.repositoryProvider.playbackManager,
                playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
            )
        },
) {
    ContextBottomSheet(
        navigator = navigator,
        stateHolder = stateHolder,
        handlePlayback = {
            handlePlayback(
                mediaGroup = arguments.mediaGroup,
                initialTrackIndex = arguments.trackIndex
            )
        }
    )
}

@RootNavGraph
@Destination(
    navArgsDelegate = ArtistContextMenuArguments::class,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun ArtistContextMenu(
    navigator: DestinationsNavigator,
    arguments: ArtistContextMenuArguments,
    handlePlayback: PlaybackHandler,
    stateHolder: StateHolder<UiState<ContextMenuState>, BaseContextMenuUserAction> =
        stateHolder { dependencyContainer ->
            ArtistContextMenuStateHolder(
                arguments = arguments,
                artistRepository = dependencyContainer.repositoryProvider.artistRepository,
            )
        }
) {
    ContextBottomSheet(
        navigator = navigator,
        stateHolder = stateHolder,
        handlePlayback = {
            handlePlayback(
                mediaGroup = MediaGroup.Artist(arguments.artistId),
                initialTrackIndex = 0
            )
        }
    )
}

@RootNavGraph
@Destination(
    navArgsDelegate = AlbumContextMenuArguments::class,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun AlbumContextMenu(
    navigator: DestinationsNavigator,
    arguments: AlbumContextMenuArguments,
    handlePlayback: PlaybackHandler,
    stateHolder: StateHolder<UiState<ContextMenuState>, BaseContextMenuUserAction> =
        stateHolder { dependencyContainer ->
            AlbumContextMenuStateHolder(
                arguments = arguments,
                albumRepository = dependencyContainer.repositoryProvider.albumRepository,
                playbackManager = dependencyContainer.repositoryProvider.playbackManager
            )
        }
) {
    ContextBottomSheet(
        navigator = navigator,
        stateHolder = stateHolder,
        handlePlayback = {
            handlePlayback(
                mediaGroup = MediaGroup.Album(arguments.albumId),
                initialTrackIndex = 0
            )
        }
    )
}

@RootNavGraph
@Destination(
    navArgsDelegate = GenreContextMenuArguments::class,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun GenreContextMenu(
    navigator: DestinationsNavigator,
    arguments: GenreContextMenuArguments,
    handlePlayback: PlaybackHandler,
    stateHolder: StateHolder<UiState<ContextMenuState>, BaseContextMenuUserAction> =
        stateHolder { dependencyContainer ->
            GenreContextMenuStateHolder(
                arguments = arguments,
                genreRepository = dependencyContainer.repositoryProvider.genreRepository,
            )
        }
) {
    ContextBottomSheet(
        navigator = navigator,
        stateHolder = stateHolder,
        handlePlayback = {
            handlePlayback(
                mediaGroup = MediaGroup.Genre(arguments.genreId),
                initialTrackIndex = 0
            )
        }
    )
}

@RootNavGraph
@Destination(
    navArgsDelegate = PlaylistContextMenuArguments::class,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun PlaylistContextMenu(
    navigator: DestinationsNavigator,
    arguments: PlaylistContextMenuArguments,
    handlePlayback: PlaybackHandler,
    stateHolder: StateHolder<UiState<ContextMenuState>, BaseContextMenuUserAction> =
        stateHolder { dependencyContainer ->
            PlaylistContextMenuStateHolder(
                arguments = arguments,
                playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
            )
        }
) {
    ContextBottomSheet(
        navigator = navigator,
        stateHolder = stateHolder,
        handlePlayback = {
            handlePlayback(
                mediaGroup = MediaGroup.Playlist(arguments.playlistId),
                initialTrackIndex = 0
            )
        }
    )
}

interface DeletePlaylistConfirmationDialogDelegate {
    fun onDismissDialog() = Unit
    fun onSubmit() = Unit
}

@Composable
fun DeletePlaylistConfirmationDialog(
    playlistName: String,
    delegate: DeletePlaylistConfirmationDialogDelegate
) {
    AlertDialog(
        onDismissRequest = { delegate.onDismissDialog() },
        confirmButton = {
            TextButton(onClick = { delegate.onSubmit() }) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = { delegate.onDismissDialog() }) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.delete_this_playlist, playlistName))
        },
        text = {
            Text(text = stringResource(id = R.string.sure_you_want_to_delete, playlistName))
        }
    )
}

@Composable
fun ContextMenuLayout(
    state: ContextMenuState,
    navigator: DestinationsNavigator,
    handlePlayback: () -> Unit,
    screenDelegate: ScreenDelegate<BaseContextMenuUserAction>,
    modifier: Modifier = Modifier
) {
    if (state.showDeleteConfirmationDialog) {
        DeletePlaylistConfirmationDialog(
            playlistName = state.menuTitle,
            delegate = object : DeletePlaylistConfirmationDialogDelegate {
                override fun onDismissDialog() {
                    screenDelegate.handle(BaseContextMenuUserAction.CancelDeleteClicked)
                }

                override fun onSubmit() {
                    screenDelegate.handle(BaseContextMenuUserAction.ConfirmDeleteClicked)
                    navigator.navigateUp()
                }
            }
        )
        // Need this to be able to dismiss bottom sheet after deleting playlist
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
        )
    } else {
        with(state) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = state.menuTitle,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    modifier = Modifier.padding(top = 12.dp)
                )

                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                LazyColumn {
                    items(listItems, key = { it.text }) {
                        when (it) {
                            ContextMenuItem.AddToPlaylist,
                            ContextMenuItem.AddToQueue,
                            ContextMenuItem.DeletePlaylist,
                            ContextMenuItem.RemoveFromPlaylist -> {
                                ContextSheetRow(state = it) {
                                    screenDelegate.handle(
                                        BaseContextMenuUserAction.RowClicked(it)
                                    )
                                }
                            }

                            ContextMenuItem.Play,
                            ContextMenuItem.PlayAllSongs,
                            ContextMenuItem.PlayFromBeginning -> {
                                ContextSheetRow(state = it) {
                                    handlePlayback()
                                }
                            }

                            is ContextMenuItem.ViewAlbum -> {
                                ContextSheetRow(state = it) {
                                    navigator.navigate(
                                        TrackListRouteDestination(
                                            TrackListArgumentsForNav(
                                                trackListType = MediaGroup.Album(
                                                    it.albumId
                                                )
                                            )
                                        )
                                    )
                                }
                            }

                            is ContextMenuItem.ViewArtist ->
                                ContextSheetRow(state = it) {
                                    navigator.navigate(
                                        ArtistRouteDestination(
                                            ArtistArguments(
                                                artistId = it.artistId
                                            )
                                        )
                                    )
                                }

                            is ContextMenuItem.ViewArtists -> {
                                ContextSheetRow(state = it) {
                                    navigator.navigate(
                                        ArtistsBottomSheetDestination(
                                            ArtistsMenuArguments(
                                                mediaType = it.mediaType,
                                                mediaId = it.mediaId
                                            )
                                        )
                                    )
                                }
                            }

                            is ContextMenuItem.ViewGenre -> {
                                ContextSheetRow(state = it) {
                                    navigator.navigate(
                                        TrackListRouteDestination(
                                            TrackListArgumentsForNav(
                                                trackListType = MediaGroup.Genre(it.genreId)
                                            )
                                        )
                                    )
                                }
                            }

                            is ContextMenuItem.ViewPlaylist -> {
                                ContextSheetRow(state = it) {
                                    navigator.navigate(
                                        TrackListRouteDestination(
                                            TrackListArgumentsForNav(
                                                trackListType = MediaGroup.Playlist(it.playlistId)
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContextSheetRow(
    state: ContextMenuItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(id = state.text),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier.clickable { onClick() },
        leadingContent = {
            Icon(
                imageVector = state.icon.icon(),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    )
}
