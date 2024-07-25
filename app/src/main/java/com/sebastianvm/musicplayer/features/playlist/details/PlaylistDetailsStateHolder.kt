package com.sebastianvm.musicplayer.features.playlist.details

import com.sebastianvm.musicplayer.core.data.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.stateHolderScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.Features
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortableListType
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.playlist.tracksearch.TrackSearchArguments
import com.sebastianvm.musicplayer.features.playlist.tracksearch.TrackSearchUiComponent
import com.sebastianvm.musicplayer.features.sort.SortMenuUiComponent
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PlaylistDetailsArguments(val playlistId: Long, val playlistName: String) : Arguments

sealed interface PlaylistDetailsState : State {
    val playlistName: String

    data class Loading(override val playlistName: String) : PlaylistDetailsState

    data class Data(
        val tracks: List<TrackRow.State>,
        val sortButtonState: SortButton.State,
        override val playlistName: String,
    ) : PlaylistDetailsState
}

sealed interface PlaylistDetailsUserAction : UserAction {
    data class TrackMoreIconClicked(val trackId: Long, val trackPositionInList: Int) :
        PlaylistDetailsUserAction

    data class TrackClicked(val trackIndex: Int) : PlaylistDetailsUserAction

    data object SortButtonClicked : PlaylistDetailsUserAction

    data object BackClicked : PlaylistDetailsUserAction

    data object AddTracksButtonClicked : PlaylistDetailsUserAction
}

class PlaylistDetailsStateHolder(
    private val args: PlaylistDetailsArguments,
    private val navController: NavController,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    sortPreferencesRepository: SortPreferencesRepository,
    private val playbackManager: PlaybackManager,
    playlistRepository: PlaylistRepository,
    private val features: Features,
) : StateHolder<PlaylistDetailsState, PlaylistDetailsUserAction> {

    override val state: StateFlow<PlaylistDetailsState> =
        combine(
                playlistRepository.getPlaylist(args.playlistId),
                sortPreferencesRepository.getPlaylistSortPreferences(args.playlistId),
            ) { playlist, sortPrefs ->
                PlaylistDetailsState.Data(
                    tracks = playlist.tracks.map { track -> TrackRow.State.fromTrack(track) },
                    playlistName = playlist.name,
                    sortButtonState =
                        sortPrefs.let {
                            SortButton.State(
                                option = sortPrefs.sortOption,
                                sortOrder = sortPrefs.sortOrder,
                            )
                        },
                )
            }
            .stateIn(
                stateHolderScope,
                SharingStarted.Lazily,
                PlaylistDetailsState.Loading(args.playlistName),
            )

    override fun handle(action: PlaylistDetailsUserAction) {
        when (action) {
            is PlaylistDetailsUserAction.TrackMoreIconClicked -> {
                navController.push(
                    TrackContextMenu(
                        arguments =
                            TrackContextMenuArguments(
                                trackId = action.trackId,
                                trackPositionInList = action.trackPositionInList,
                                trackList = MediaGroup.Playlist(playlistId = args.playlistId),
                            ),
                        navController = navController,
                        features = features),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is PlaylistDetailsUserAction.SortButtonClicked -> {
                navController.push(
                    SortMenuUiComponent(
                        arguments =
                            SortMenuArguments(
                                listType = SortableListType.Playlist(args.playlistId))),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is PlaylistDetailsUserAction.BackClicked -> {
                navController.pop()
            }
            is PlaylistDetailsUserAction.TrackClicked -> {
                stateHolderScope.launch {
                    playbackManager.playMedia(
                        mediaGroup = MediaGroup.Playlist(args.playlistId),
                        initialTrackIndex = action.trackIndex,
                    )
                }
            }
            is PlaylistDetailsUserAction.AddTracksButtonClicked -> {
                navController.push(
                    TrackSearchUiComponent(
                        arguments = TrackSearchArguments(playlistId = args.playlistId),
                        navController = navController,
                    ))
            }
        }
    }
}
