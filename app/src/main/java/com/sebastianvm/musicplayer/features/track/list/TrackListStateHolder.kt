package com.sebastianvm.musicplayer.features.track.list

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.database.entities.TrackListMetadata
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.sort.SortMenu
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortableListType
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.rememberStateHolder
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TrackListArguments(val trackListType: TrackList) : Arguments

data class TrackListState(
    val modelListState: ModelListState,
    val trackListType: TrackList,
) : State

sealed interface TrackListUserAction : UserAction {
    data class TrackMoreIconClicked(val trackId: Long, val trackPositionInList: Int) :
        TrackListUserAction

    data class TrackClicked(val trackIndex: Int) : TrackListUserAction
    data object SortButtonClicked : TrackListUserAction
    data object BackClicked : TrackListUserAction
}

class TrackListStateHolder(
    private val args: TrackListArguments,
    private val navController: NavController,
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
    trackRepository: TrackRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    private val playbackManager: PlaybackManager,
) : StateHolder<UiState<TrackListState>, TrackListUserAction> {

    override val state: StateFlow<UiState<TrackListState>> = combine(
        trackRepository.getTrackListWithMetaData(args.trackListType),
        args.trackListType.takeUnless { it is MediaGroup.Album }?.let {
            sortPreferencesRepository.getTrackListSortPreferences(args.trackListType)
        } ?: flowOf(null),
    ) { trackListWithMetadata, sortPrefs ->
        if (trackListWithMetadata.trackList.isEmpty()) {
            Empty
        } else {
            Data(
                TrackListState(
                    modelListState = ModelListState(
                        items = trackListWithMetadata.trackList.map { track -> track.toModelListItemState() },
                        headerState = trackListWithMetadata.metaData.toHeaderState(),
                        sortButtonState = sortPrefs?.let {
                            SortButtonState(
                                text = sortPrefs.sortOption.stringId,
                                sortOrder = sortPrefs.sortOrder
                            )
                        }
                    ),
                    trackListType = args.trackListType,
                )
            )
        }
    }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: TrackListUserAction) {
        when (action) {
            is TrackListUserAction.TrackMoreIconClicked -> {
                navController.push(
                    TrackContextMenu(
                        arguments = TrackContextMenuArguments(
                            trackId = action.trackId,
                            trackPositionInList = action.trackPositionInList,
                            trackList = args.trackListType,
                        ),
                        navController
                    ),
                    navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet)
                )
            }

            is TrackListUserAction.SortButtonClicked -> {
                navController.push(
                    SortMenu(
                        arguments = SortMenuArguments(
                            listType = SortableListType.Tracks(
                                trackList = args.trackListType
                            )
                        ),
                    ),
                    navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet)
                )
            }

            is TrackListUserAction.BackClicked -> {
                navController.pop()
            }

            is TrackListUserAction.TrackClicked -> {
                stateHolderScope.launch {
                    playbackManager.playMedia(
                        mediaGroup = args.trackListType,
                        initialTrackIndex = action.trackIndex
                    )
                }
            }
        }
    }
}

fun TrackListMetadata?.toHeaderState(): HeaderState {
    return when {
        this == null -> HeaderState.None
        mediaArtImageState != null -> {
            HeaderState.WithImage(
                title = trackListName,
                imageState = mediaArtImageState
            )
        }

        else -> HeaderState.Simple(title = trackListName)
    }
}

@Composable
fun rememberTrackListStateHolder(
    args: TrackListArguments,
    navController: NavController
): TrackListStateHolder {
    return rememberStateHolder { dependencies ->
        TrackListStateHolder(
            args = args,
            navController = navController,
            trackRepository = dependencies.repositoryProvider.trackRepository,
            sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
            playbackManager = dependencies.repositoryProvider.playbackManager
        )
    }
}
