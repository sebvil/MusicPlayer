package com.sebastianvm.musicplayer.features.track.list

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.data.track.TrackRepository
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.playback.manager.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortableListType
import com.sebastianvm.musicplayer.features.api.sort.sortMenu
import com.sebastianvm.musicplayer.features.api.track.list.TrackListProps
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuProps
import com.sebastianvm.musicplayer.features.api.track.menu.trackContextMenu
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.registry.create
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
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

class TrackListViewModel(
    private val props: StateFlow<TrackListProps>,
    vmScope: CoroutineScope = getViewModelScope(),
    trackRepository: TrackRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) : BaseViewModel<TrackListState, TrackListUserAction>(viewModelScope = vmScope) {

    private val navController: NavController
        get() = props.value.navController

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
            .stateIn(viewModelScope, SharingStarted.Lazily, TrackListState.Loading)

    override fun handle(action: TrackListUserAction) {
        when (action) {
            is TrackListUserAction.TrackMoreIconClicked -> {
                navController.push(
                    features
                        .trackContextMenu()
                        .create(
                            arguments =
                                TrackContextMenuArguments(
                                    trackId = action.trackId,
                                    trackPositionInList = action.trackPositionInList,
                                    trackList = MediaGroup.AllTracks,
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
            is TrackListUserAction.SortButtonClicked -> {
                navController.push(
                    features
                        .sortMenu()
                        .create(
                            arguments = SortMenuArguments(listType = SortableListType.AllTracks)
                        ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is TrackListUserAction.TrackClicked -> {
                viewModelScope.launch {
                    playbackManager.playMedia(
                        mediaGroup = MediaGroup.AllTracks,
                        initialTrackIndex = action.trackIndex,
                    )
                }
            }
        }
    }
}
