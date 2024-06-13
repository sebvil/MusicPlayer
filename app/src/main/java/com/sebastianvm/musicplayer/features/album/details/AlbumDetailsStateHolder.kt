package com.sebastianvm.musicplayer.features.album.details

import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
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

data class AlbumDetailsArguments(val albumId: Long, val albumName: String, val imageUri: String) :
    Arguments

sealed interface AlbumDetailsState : State {
    val albumName: String
    val imageUri: String

    data class Loading(override val albumName: String, override val imageUri: String) :
        AlbumDetailsState

    data class Data(
        val tracks: List<TrackRow.State>,
        override val albumName: String,
        override val imageUri: String,
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
) : StateHolder<AlbumDetailsState, AlbumDetailsUserAction> {

    override val state: StateFlow<AlbumDetailsState> =
        albumRepository
            .getAlbum(args.albumId)
            .map { album ->
                AlbumDetailsState.Data(
                    tracks = album.tracks.map { track -> TrackRow.State.fromTrack(track) },
                    albumName = args.albumName,
                    imageUri = args.imageUri,
                )
            }
            .stateIn(
                stateHolderScope,
                SharingStarted.Lazily,
                AlbumDetailsState.Loading(albumName = args.albumName, imageUri = args.imageUri)
            )

    override fun handle(action: AlbumDetailsUserAction) {
        when (action) {
            is AlbumDetailsUserAction.TrackMoreIconClicked -> {
                navController.push(
                    TrackContextMenu(
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
