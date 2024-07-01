package com.sebastianvm.musicplayer.features.album.menu

import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.data.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistUiComponent
import com.sebastianvm.musicplayer.features.artistsmenu.ArtistsMenu
import com.sebastianvm.musicplayer.features.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AlbumContextMenuArguments(val albumId: Long) : Arguments

sealed interface AlbumContextMenuState : State {
    data class Data(val albumName: String, val albumId: Long, val viewArtistsState: ViewArtistRow) :
        AlbumContextMenuState

    data object Loading : AlbumContextMenuState
}

sealed interface ViewArtistRow {
    data class SingleArtist(val artistId: Long) : ViewArtistRow

    data object MultipleArtists : ViewArtistRow

    data object NoArtists : ViewArtistRow
}

sealed interface AlbumContextMenuUserAction : UserAction {
    data object PlayAlbumClicked : AlbumContextMenuUserAction

    data object ViewArtistsClicked : AlbumContextMenuUserAction

    data class ViewArtistClicked(val artistId: Long) : AlbumContextMenuUserAction
}

class AlbumContextMenuStateHolder(
    arguments: AlbumContextMenuArguments,
    albumRepository: AlbumRepository,
    private val playbackManager: PlaybackManager,
    private val navController: NavController,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<AlbumContextMenuState, AlbumContextMenuUserAction> {

    private val albumId = arguments.albumId

    override val state: StateFlow<AlbumContextMenuState> =
        albumRepository
            .getAlbumWithArtists(albumId)
            .map { album ->
                AlbumContextMenuState.Data(
                    albumName = album.title,
                    albumId = albumId,
                    viewArtistsState =
                        when (album.artists.size) {
                            0 -> ViewArtistRow.NoArtists
                            1 -> ViewArtistRow.SingleArtist(album.artists[0].id)
                            else -> ViewArtistRow.MultipleArtists
                        },
                )
            }
            .stateIn(stateHolderScope, SharingStarted.Lazily, AlbumContextMenuState.Loading)

    override fun handle(action: AlbumContextMenuUserAction) {
        when (action) {
            AlbumContextMenuUserAction.PlayAlbumClicked -> {
                stateHolderScope.launch {
                    playbackManager.playMedia(mediaGroup = MediaGroup.Album(albumId))
                }
            }
            is AlbumContextMenuUserAction.ViewArtistClicked -> {
                navController.push(
                    ArtistUiComponent(
                        arguments = ArtistArguments(action.artistId),
                        navController = navController,
                    ),
                    navOptions = NavOptions(popCurrent = true),
                )
            }
            AlbumContextMenuUserAction.ViewArtistsClicked -> {
                navController.push(
                    ArtistsMenu(
                        arguments = ArtistsMenuArguments(MediaGroup.Album(albumId)),
                        navController = navController,
                    ),
                    navOptions =
                        NavOptions(
                            popCurrent = true,
                            presentationMode = NavOptions.PresentationMode.BottomSheet,
                        ),
                )
            }
        }
    }
}
