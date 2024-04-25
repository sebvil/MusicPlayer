package com.sebastianvm.musicplayer.ui.library.tracklist

import com.sebastianvm.musicplayer.database.entities.TrackListMetadata
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable

class TrackListStateHolder(
    private val args: TrackListArguments,
    stateHolderScope: CoroutineScope = stateHolderScope(),
    trackRepository: TrackRepository,
    sortPreferencesRepository: SortPreferencesRepository,
) : StateHolder<UiState<TrackListState>, TrackListUserAction> {

    override val state: StateFlow<UiState<TrackListState>> = combine(
        trackRepository.getTrackListWithMetaData(args.trackListType),
        sortPreferencesRepository.getTrackListSortPreferences(args.trackListType)
    ) { trackListWithMetadata, sortPrefs ->
        if (trackListWithMetadata.trackList.isEmpty()) {
            Empty
        } else {
            Data(
                TrackListState(
                    modelListState = ModelListState(
                        items = trackListWithMetadata.trackList.map { track -> track.toModelListItemState() },
                        headerState = trackListWithMetadata.metaData.toHeaderState(),
                        sortButtonState = SortButtonState(
                            text = sortPrefs.sortOption.stringId,
                            sortOrder = sortPrefs.sortOrder
                        )
                    ),
                    trackListType = args.trackListType
                )
            )
        }
    }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: TrackListUserAction) = Unit
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
) : State

sealed interface TrackListUserAction : UserAction


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
