package com.sebastianvm.musicplayer.features.track.list

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.database.entities.TrackListMetadata
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortMenuStateHolder
import com.sebastianvm.musicplayer.features.sort.SortableListType
import com.sebastianvm.musicplayer.features.sort.sortMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.track.menu.SourceTrackList
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuStateHolder
import com.sebastianvm.musicplayer.features.track.menu.trackContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Delegate
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolder
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

interface TrackListDelegate : Delegate, NavController

class TrackListStateHolder(
    private val args: TrackListArguments,
    private val delegate: TrackListDelegate,
    stateHolderScope: CoroutineScope = stateHolderScope(),
    trackRepository: TrackRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    sortMenuStateHolderFactory: StateHolderFactory<SortMenuArguments, SortMenuStateHolder>,
    private val trackContextMenuStateHolderFactory: StateHolderFactory<TrackContextMenuArguments, TrackContextMenuStateHolder>
) : StateHolder<UiState<TrackListState>, TrackListUserAction> {

    private val contextMenuTrackId = MutableStateFlow<Long?>(null)

    private val sortMenuStateHolder by lazy {
        sortMenuStateHolderFactory.getStateHolder(
            SortMenuArguments(SortableListType.Tracks(args.trackListType))
        )
    }

    private val showSortMenu = MutableStateFlow(false)
    override val state: StateFlow<UiState<TrackListState>> = combine(
        trackRepository.getTrackListWithMetaData(args.trackListType),
        args.trackListType.takeUnless { it is MediaGroup.Album }?.let {
            sortPreferencesRepository.getTrackListSortPreferences(args.trackListType)
        } ?: flowOf(null),
        contextMenuTrackId,
        showSortMenu
    ) { trackListWithMetadata, sortPrefs, contextMenuTrackId, showSortMenu ->
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
                    trackContextMenuStateHolder = contextMenuTrackId?.let {
                        trackContextMenuStateHolderFactory.getStateHolder(
                            TrackContextMenuArguments(
                                trackId = it,
                                trackList = when (args.trackListType) {
                                    is MediaGroup.Album -> SourceTrackList.Album
                                    MediaGroup.AllTracks -> SourceTrackList.AllTracks
                                    is MediaGroup.Genre -> SourceTrackList.Genre
                                    is MediaGroup.Playlist -> SourceTrackList.Playlist(
                                        args.trackListType,
                                        -1
                                    ) // TODO handle position
                                }
                            )
                        )
                    },
                    sortMenuStateHolder = if (showSortMenu) {
                        sortMenuStateHolder
                    } else {
                        null
                    }
                )
            )
        }
    }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: TrackListUserAction) {
        when (action) {
            is TrackListUserAction.TrackMoreIconClicked -> {
                contextMenuTrackId.update { action.trackId }
            }

            TrackListUserAction.TrackContextMenuDismissed -> {
                contextMenuTrackId.update { null }
            }

            is TrackListUserAction.SortButtonClicked -> {
                showSortMenu.update { true }
            }

            is TrackListUserAction.SortMenuDismissed -> {
                showSortMenu.update { false }
            }

            is TrackListUserAction.BackClicked -> {
                delegate.pop()
            }
        }
    }
}

data class TrackListArguments(val trackListType: TrackList) : Arguments

data class TrackListState(
    val modelListState: ModelListState,
    val trackListType: TrackList,
    val trackContextMenuStateHolder: TrackContextMenuStateHolder?,
    val sortMenuStateHolder: SortMenuStateHolder?
) : State

sealed interface TrackListUserAction : UserAction {
    data class TrackMoreIconClicked(val trackId: Long) : TrackListUserAction
    data object TrackContextMenuDismissed : TrackListUserAction
    data object SortButtonClicked : TrackListUserAction
    data object SortMenuDismissed : TrackListUserAction
    data object BackClicked : TrackListUserAction
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
    val sortMenuStateHolderFactory = sortMenuStateHolderFactory()
    val trackContextMenuStateHolderFactory = trackContextMenuStateHolderFactory()
    return stateHolder { dependencies ->
        TrackListStateHolder(
            args = args,
            delegate = object : TrackListDelegate, NavController by navController {},
            trackRepository = dependencies.repositoryProvider.trackRepository,
            sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
            sortMenuStateHolderFactory = sortMenuStateHolderFactory,
            trackContextMenuStateHolderFactory = trackContextMenuStateHolderFactory
        )
    }
}