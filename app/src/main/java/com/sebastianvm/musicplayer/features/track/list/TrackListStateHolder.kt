package com.sebastianvm.musicplayer.features.track.list

import com.sebastianvm.musicplayer.designsystem.components.SortButton
import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortMenuUiComponent
import com.sebastianvm.musicplayer.features.sort.SortableListType
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.model.TrackListMetadata
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TrackListArguments(val trackListType: TrackList) : Arguments

sealed interface TrackListState : State {
    data object Loading : TrackListState

    data class Data(
        val tracks: List<TrackRow.State>,
        val sortButtonState: SortButton.State?,
        val headerState: Header.State,
        val trackListType: TrackList,
    ) : TrackListState
}

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
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    trackRepository: TrackRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    private val playbackManager: PlaybackManager,
) : StateHolder<TrackListState, TrackListUserAction> {

    private val sortPreferences =
        when (args.trackListType) {
            is MediaGroup.Album -> {
                flowOf(null)
            }
            is MediaGroup.Playlist -> {
                sortPreferencesRepository.getPlaylistSortPreferences(args.trackListType.playlistId)
            }
            is MediaGroup.Genre,
            is MediaGroup.AllTracks -> {
                sortPreferencesRepository.getTrackListSortPreferences(args.trackListType)
            }
        }

    override val state: StateFlow<TrackListState> =
        combine(trackRepository.getTrackListWithMetaData(args.trackListType), sortPreferences) {
                trackListWithMetadata,
                sortPrefs ->
                TrackListState.Data(
                    tracks =
                        trackListWithMetadata.trackList.map { track ->
                            TrackRow.State.fromTrack(track)
                        },
                    headerState = trackListWithMetadata.metaData.toHeaderState(),
                    sortButtonState =
                        sortPrefs?.let {
                            SortButton.State(
                                text = sortPrefs.sortOption.stringId,
                                sortOrder = sortPrefs.sortOrder,
                            )
                        },
                    trackListType = args.trackListType,
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
                                trackList = args.trackListType,
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
                        arguments =
                            SortMenuArguments(
                                listType =
                                    when (args.trackListType) {
                                        is MediaGroup.AllTracks -> SortableListType.AllTracks
                                        is MediaGroup.Genre ->
                                            SortableListType.Genre(args.trackListType.genreId)
                                        is MediaGroup.Playlist ->
                                            SortableListType.Playlist(args.trackListType.playlistId)
                                        is MediaGroup.Album ->
                                            error("Cannot sort ${args.trackListType}")
                                    }
                            )
                    ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is TrackListUserAction.BackClicked -> {
                navController.pop()
            }
            is TrackListUserAction.TrackClicked -> {
                stateHolderScope.launch {
                    playbackManager.playMedia(
                        mediaGroup = args.trackListType,
                        initialTrackIndex = action.trackIndex,
                    )
                }
            }
        }
    }
}

fun TrackListMetadata?.toHeaderState(): Header.State {
    return when {
        this == null -> Header.State.None
        mediaArtImageState != null -> {
            Header.State.WithImage(title = trackListName, imageState = mediaArtImageState)
        }
        else -> Header.State.Simple(title = trackListName)
    }
}
