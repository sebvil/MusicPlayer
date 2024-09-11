package com.sebastianvm.musicplayer.features.album.details

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsProps
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuProps
import com.sebastianvm.musicplayer.features.api.track.menu.trackContextMenu
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.registry.create
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AlbumDetailsState : State {
    val albumName: String
    val imageUri: String
    val artists: String?

    data class Loading(
        override val albumName: String,
        override val imageUri: String,
        override val artists: String?,
    ) : AlbumDetailsState

    data class Data(
        val tracks: List<TrackRow.State>,
        override val albumName: String,
        override val imageUri: String,
        override val artists: String?,
    ) : AlbumDetailsState
}

sealed interface AlbumDetailsUserAction : UserAction {
    data class TrackMoreIconClicked(val trackId: Long, val trackPositionInList: Int) :
        AlbumDetailsUserAction

    data class TrackClicked(val trackIndex: Int) : AlbumDetailsUserAction

    data object BackClicked : AlbumDetailsUserAction
}

class AlbumDetailsViewModel(
    private val arguments: AlbumDetailsArguments,
    private val props: StateFlow<AlbumDetailsProps>,
    vmScope: CoroutineScope = getViewModelScope(),
    albumRepository: AlbumRepository,
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) : BaseViewModel<AlbumDetailsState, AlbumDetailsUserAction>(viewModelScope = vmScope) {

    private val navController: NavController
        get() = props.value.navController

    override val state: StateFlow<AlbumDetailsState> =
        albumRepository
            .getAlbum(arguments.albumId)
            .map { album ->
                AlbumDetailsState.Data(
                    tracks = album.tracks.map { track -> TrackRow.State.fromTrack(track) },
                    albumName = album.title,
                    imageUri = album.imageUri,
                    artists = album.artists.takeIf { it.isNotEmpty() }?.joinToString { it.name },
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                AlbumDetailsState.Loading(
                    albumName = arguments.albumName,
                    imageUri = arguments.imageUri,
                    artists = arguments.artists,
                ),
            )

    override fun handle(action: AlbumDetailsUserAction) {
        when (action) {
            is AlbumDetailsUserAction.TrackMoreIconClicked -> {
                navController.push(
                    features
                        .trackContextMenu()
                        .create(
                            arguments =
                                TrackContextMenuArguments(
                                    trackId = action.trackId,
                                    trackPositionInList = action.trackPositionInList,
                                    trackList = MediaGroup.Album(arguments.albumId),
                                ),
                            props =
                                MutableStateFlow(
                                    TrackContextMenuProps(navController = navController)
                                ),
                        ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is AlbumDetailsUserAction.BackClicked -> {
                navController.pop()
            }
            is AlbumDetailsUserAction.TrackClicked -> {
                viewModelScope.launch {
                    playbackManager.playMedia(
                        mediaGroup = MediaGroup.Album(arguments.albumId),
                        initialTrackIndex = action.trackIndex,
                    )
                }
            }
        }
    }
}
