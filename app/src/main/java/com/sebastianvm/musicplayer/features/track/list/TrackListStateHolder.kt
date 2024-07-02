package com.sebastianvm.musicplayer.features.track.list

import com.sebastianvm.musicplayer.core.data.track.TrackRepository
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.playback.manager.PlaybackManager
import com.sebastianvm.musicplayer.designsystem.components.SortButton
import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortMenuUiComponent
import com.sebastianvm.musicplayer.features.sort.SortableListType
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
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

sealed interface TrackListState : State {
    data object Loading : TrackListState

    data class Data(val tracks: List<TrackRow.State>, val sortButtonState: SortButton.State) :
        TrackListState
}

sealed interface TrackListUserAction : UserAction {
    data class TrackMoreIconClicked(val trackId: Long, val trackPositionInList: Int) :
        TrackListUserAction

    data class TrackClicked(val trackIndex: Int) : TrackListUserAction

    data object SortButtonClicked : TrackListUserAction
}

class TrackListStateHolder(
    private val navController: NavController,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    trackRepository: TrackRepository,
    sortPreferencesRepository:
        com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository,
    private val playbackManager: PlaybackManager,
) : StateHolder<TrackListState, TrackListUserAction> {

    private val sortPreferences =
        sortPreferencesRepository.getTrackListSortPreferences(MediaGroup.AllTracks)

    override val state: StateFlow<TrackListState> =
        combine(trackRepository.getTracksForMedia(MediaGroup.AllTracks), sortPreferences) {
                tracks,
                sortPrefs ->
                TrackListState.Data(
                    tracks = tracks.map { track -> TrackRow.State.fromTrack(track) },
                    sortButtonState =
                        SortButton.State(
                            option = sortPrefs.sortOption,
                            sortOrder = sortPrefs.sortOrder,
                        ),
                )
            }
            .stateIn(stateHolderScope, SharingStarted.Lazily, TrackListState.Loading)

    override fun handle(action: TrackListUserAction) {
        when (action) {
            is TrackListUserAction.TrackMoreIconClicked -> {
                navController.push(
                    TrackContextMenu(
                        arguments =
                            TrackContextMenuArguments(
                                trackId = action.trackId,
                                trackPositionInList = action.trackPositionInList,
                                trackList = MediaGroup.AllTracks,
                            ),
                        navController = navController,
                    ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is TrackListUserAction.SortButtonClicked -> {
                navController.push(
                    SortMenuUiComponent(
                        arguments = SortMenuArguments(listType = SortableListType.AllTracks)),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is TrackListUserAction.TrackClicked -> {
                stateHolderScope.launch {
                    playbackManager.playMedia(
                        mediaGroup = MediaGroup.AllTracks,
                        initialTrackIndex = action.trackIndex,
                    )
                }
            }
        }
    }
}
