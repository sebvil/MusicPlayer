package com.sebastianvm.musicplayer.features.track.list

import com.sebastianvm.musicplayer.database.entities.TrackListMetadata
import com.sebastianvm.musicplayer.features.track.menu.SourceTrackList
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuStateHolder
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable

class TrackListStateHolder(
    private val args: TrackListArguments,
    stateHolderScope: CoroutineScope = stateHolderScope(),
    trackRepository: TrackRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    private val trackContextMenuStateHolderFactory: TrackContextMenuStateHolderFactory
) : StateHolder<UiState<TrackListState>, TrackListUserAction> {

    private val contextMenuTrackId = MutableStateFlow<Long?>(null)

    override val state: StateFlow<UiState<TrackListState>> = combine(
        trackRepository.getTrackListWithMetaData(args.trackListType),
        args.trackListType.takeUnless { it is MediaGroup.Album }?.let {
            sortPreferencesRepository.getTrackListSortPreferences(args.trackListType)
        } ?: flowOf(null),
        contextMenuTrackId
    ) { trackListWithMetadata, sortPrefs, contextMenuTrackId ->
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
        }
    }
}

@Serializable
data class TrackListArgumentsForNav(val trackListType: TrackList?) {
    fun toTrackListArguments() =
        TrackListArguments(trackListType ?: MediaGroup.AllTracks)
}

data class TrackListArguments(val trackListType: TrackList)

data class TrackListState(
    val modelListState: ModelListState,
    val trackListType: TrackList,
    val trackContextMenuStateHolder: TrackContextMenuStateHolder?
) : State

sealed interface TrackListUserAction : UserAction {
    data class TrackMoreIconClicked(val trackId: Long) : TrackListUserAction
    data object TrackContextMenuDismissed : TrackListUserAction
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
