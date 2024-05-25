package com.sebastianvm.musicplayer.features.album.menu

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistScreen
import com.sebastianvm.musicplayer.features.artistsmenu.ArtistsMenu
import com.sebastianvm.musicplayer.features.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.track.list.TrackList
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.model.MediaWithArtists
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.rememberStateHolder
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AlbumContextMenuArguments(val albumId: Long) : Arguments

sealed interface AlbumContextMenuState : State {
    data class Data(
        val albumName: String,
        val albumId: Long,
        val viewArtistsState: ViewArtistRow
    ) : AlbumContextMenuState

    data object Loading : AlbumContextMenuState
}

sealed interface ViewArtistRow {
    data class SingleArtist(val artistId: Long) : ViewArtistRow
    data object MultipleArtists : ViewArtistRow
    data object NoArtists : ViewArtistRow
}

sealed interface AlbumContextMenuUserAction : UserAction {
    data object AddToQueueClicked : AlbumContextMenuUserAction
    data object PlayAlbumClicked : AlbumContextMenuUserAction
    data object ViewArtistsClicked : AlbumContextMenuUserAction
    data class ViewArtistClicked(val artistId: Long) : AlbumContextMenuUserAction
    data object ViewAlbumClicked : AlbumContextMenuUserAction
}

class AlbumContextMenuStateHolder(
    arguments: AlbumContextMenuArguments,
    albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository,
    private val playbackManager: PlaybackManager,
    private val navController: NavController,
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<AlbumContextMenuState, AlbumContextMenuUserAction> {

    private val albumId = arguments.albumId

    override val state: StateFlow<AlbumContextMenuState> =
        albumRepository.getFullAlbumInfo(albumId).map { album ->
            AlbumContextMenuState.Data(
                albumName = album.album.albumName,
                albumId = albumId,
                viewArtistsState = when (album.artists.size) {
                    0 -> ViewArtistRow.NoArtists
                    1 -> ViewArtistRow.SingleArtist(album.artists[0])
                    else -> ViewArtistRow.MultipleArtists
                }
            )
        }.stateIn(stateHolderScope, SharingStarted.Lazily, AlbumContextMenuState.Loading)

    override fun handle(action: AlbumContextMenuUserAction) {
        when (action) {
            is AlbumContextMenuUserAction.AddToQueueClicked -> {
                stateHolderScope.launch {
                    val tracks =
                        trackRepository.getTracksForMedia(MediaGroup.Album(albumId)).first()
                    playbackManager.addToQueue(tracks)
                }
            }

            AlbumContextMenuUserAction.PlayAlbumClicked -> {
            }

            AlbumContextMenuUserAction.ViewAlbumClicked -> {
                navController.push(
                    TrackList(
                        arguments = TrackListArguments(MediaGroup.Album(albumId = albumId)),
                        navController = navController
                    ),
                    navOptions = NavOptions(popCurrent = true)
                )
            }

            is AlbumContextMenuUserAction.ViewArtistClicked -> {
                navController.push(
                    ArtistScreen(
                        arguments = ArtistArguments(action.artistId),
                        navController = navController
                    ),
                    navOptions = NavOptions(popCurrent = true)
                )
            }

            AlbumContextMenuUserAction.ViewArtistsClicked -> {
                navController.push(
                    ArtistsMenu(
                        arguments = ArtistsMenuArguments(
                            mediaType = MediaWithArtists.Album,
                            mediaId = albumId
                        ),
                        navController = navController
                    ),
                    navOptions = NavOptions(
                        popCurrent = true,
                        presentationMode = NavOptions.PresentationMode.BottomSheet
                    )
                )
            }
        }
    }
}

@Composable
fun rememberAlbumContextMenuStateHolder(
    arguments: AlbumContextMenuArguments,
    navController: NavController
): AlbumContextMenuStateHolder {
    return rememberStateHolder { dependencyContainer ->
        AlbumContextMenuStateHolder(
            arguments = arguments,
            albumRepository = dependencyContainer.repositoryProvider.albumRepository,
            trackRepository = dependencyContainer.repositoryProvider.trackRepository,
            playbackManager = dependencyContainer.repositoryProvider.playbackManager,
            navController = navController,
        )
    }
}
