package com.sebastianvm.musicplayer.features.album.menu

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.api.artist.details.artistDetails
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.api.artistsmenu.artistsMenu
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

class AlbumContextMenuViewModel(
    arguments: AlbumContextMenuArguments,
    albumRepository: AlbumRepository,
    private val playbackManager: PlaybackManager,
    private val navController: NavController,
    vmScope: CoroutineScope = getViewModelScope(),
    private val features: FeatureRegistry,
) : BaseViewModel<AlbumContextMenuState, AlbumContextMenuUserAction>(viewModelScope = vmScope) {

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
            .stateIn(viewModelScope, SharingStarted.Lazily, AlbumContextMenuState.Loading)

    override fun handle(action: AlbumContextMenuUserAction) {
        when (action) {
            AlbumContextMenuUserAction.PlayAlbumClicked -> {
                viewModelScope.launch {
                    playbackManager.playMedia(mediaGroup = MediaGroup.Album(albumId))
                }
            }
            is AlbumContextMenuUserAction.ViewArtistClicked -> {
                navController.push(
                    features
                        .artistDetails()
                        .artistDetailsUiComponent(
                            arguments = ArtistDetailsArguments(action.artistId),
                            navController = navController,
                        ),
                    navOptions = NavOptions(popCurrent = true),
                )
            }
            AlbumContextMenuUserAction.ViewArtistsClicked -> {
                navController.push(
                    features
                        .artistsMenu()
                        .artistsMenuUiComponent(
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
