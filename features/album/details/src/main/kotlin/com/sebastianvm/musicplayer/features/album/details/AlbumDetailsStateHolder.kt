package com.sebastianvm.musicplayer.features.album.details

import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.stateHolderScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.api.track.menu.trackContextMenu
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import kotlinx.coroutines.CoroutineScope
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

class AlbumDetailsStateHolder(
    private val args: AlbumDetailsArguments,
    private val navController: NavController,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    albumRepository: AlbumRepository,
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) : StateHolder<AlbumDetailsState, AlbumDetailsUserAction> {

    override val state: StateFlow<AlbumDetailsState> =
        albumRepository
            .getAlbum(args.albumId)
            .map { album ->
                AlbumDetailsState.Data(
                    tracks = album.tracks.map { track -> TrackRow.State.fromTrack(track) },
                    albumName = album.title,
                    imageUri = album.imageUri,
                    artists = album.artists.takeIf { it.isNotEmpty() }?.joinToString { it.name },
                )
            }
            .stateIn(
                stateHolderScope,
                SharingStarted.Lazily,
                AlbumDetailsState.Loading(
                    albumName = args.albumName,
                    imageUri = args.imageUri,
                    artists = args.artists,
                ),
            )

    override fun handle(action: AlbumDetailsUserAction) {
        when (action) {
            is AlbumDetailsUserAction.TrackMoreIconClicked -> {
                navController.push(
                    features
                        .trackContextMenu()
                        .trackContextMenuUiComponent(
                            arguments =
                                TrackContextMenuArguments(
                                    trackId = action.trackId,
                                    trackPositionInList = action.trackPositionInList,
                                    trackList = MediaGroup.Album(args.albumId),
                                ),
                            navController = navController,
                        ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is AlbumDetailsUserAction.BackClicked -> {
                navController.pop()
            }
            is AlbumDetailsUserAction.TrackClicked -> {
                stateHolderScope.launch {
                    playbackManager.playMedia(
                        mediaGroup = MediaGroup.Album(args.albumId),
                        initialTrackIndex = action.trackIndex,
                    )
                }
            }
        }
    }
}
