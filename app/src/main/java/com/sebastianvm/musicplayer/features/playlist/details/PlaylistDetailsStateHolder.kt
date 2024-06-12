package com.sebastianvm.musicplayer.features.playlist.details

import com.sebastianvm.musicplayer.designsystem.components.SortButton
import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.playlist.tracksearch.TrackSearchArguments
import com.sebastianvm.musicplayer.features.playlist.tracksearch.TrackSearchUiComponent
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortMenuUiComponent
import com.sebastianvm.musicplayer.features.sort.SortableListType
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
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
    trackRepository: TrackRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    private val playbackManager: PlaybackManager,
    playlistRepository: PlaylistRepository,
) : StateHolder<PlaylistDetailsState, PlaylistDetailsUserAction> {

    override val state: StateFlow<PlaylistDetailsState> =
        combine(
                trackRepository.getTracksForMedia(
                    mediaGroup = MediaGroup.Playlist(args.playlistId)
                ),
                playlistRepository.getPlaylistName(args.playlistId),
                sortPreferencesRepository.getPlaylistSortPreferences(args.playlistId)
            ) { tracks, playlistName, sortPrefs ->
                PlaylistDetailsState.Data(
                    tracks = tracks.map { track -> TrackRow.State.fromTrack(track) },
                    playlistName = playlistName,
                    sortButtonState =
                        sortPrefs.let {
                            SortButton.State(
                                text = sortPrefs.sortOption.stringId,
                                sortOrder = sortPrefs.sortOrder,
                            )
                        },
                )
            }
            .stateIn(
                stateHolderScope,
                SharingStarted.Lazily,
                PlaylistDetailsState.Loading(args.playlistName)
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
                                trackList = MediaGroup.Playlist(playlistId = args.playlistId)
                            ),
                        navController = navController,
                    ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is PlaylistDetailsUserAction.SortButtonClicked -> {
                navController.push(
                    SortMenuUiComponent(
                        arguments =
                            SortMenuArguments(listType = SortableListType.Playlist(args.playlistId))
                    ),
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
                        arguments =
                            TrackSearchArguments(
                                playlistId = args.playlistId,
                            ),
                        navController = navController,
                    ),
                )
            }
        }
    }
}
