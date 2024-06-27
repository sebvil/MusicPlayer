package com.sebastianvm.musicplayer.features.album.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import app.cash.molecule.RecompositionMode
import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseStateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class AlbumDetailsArguments(
    val albumId: Long,
    val albumName: String,
    val imageUri: String,
    val artists: String?,
) : Arguments

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
    private val albumRepository: AlbumRepository,
    private val playbackManager: PlaybackManager,
    stateHolderScope: CoroutineScope,
    recompositionMode: RecompositionMode = RecompositionMode.ContextClock,
) :
    BaseStateHolder<AlbumDetailsState, AlbumDetailsUserAction>(
        stateHolderScope = stateHolderScope,
        recompositionMode = recompositionMode,
    ) {

    @Composable
    override fun presenter(): AlbumDetailsState {
        val albumState = albumRepository.getAlbum(args.albumId).collectAsState(initial = null)
        val album = albumState.value
        return if (album == null) {
            AlbumDetailsState.Loading(
                albumName = args.albumName,
                imageUri = args.imageUri,
                artists = args.artists,
            )
        } else {
            AlbumDetailsState.Data(
                tracks = album.tracks.map { track -> TrackRow.State.fromTrack(track) },
                albumName = album.title,
                imageUri = album.imageUri,
                artists = album.artists.takeIf { it.isNotEmpty() }?.joinToString { it.name },
            )
        }
    }

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
