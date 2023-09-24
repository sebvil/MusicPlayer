package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.database.entities.TrackListMetadata
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TrackListViewModel
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
constructor(
    initialState: TrackListState,
    viewModelScope: CoroutineScope?,
    trackRepository: TrackRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    private val args: TrackListArguments,
) : BaseViewModel<TrackListState, TrackListUserAction>(
    initialState = initialState,
    viewModelScope = viewModelScope
) {

    @Inject
    constructor(
        trackRepository: TrackRepository,
        sortPreferencesRepository: SortPreferencesRepository,
        args: TrackListArguments
    ) : this(
        initialState = TrackListState(
            trackListType = args.trackListType,
            modelListState = ModelListState(
                items = listOf(),
                sortButtonState = null,
                headerState = HeaderState.None
            ),
            isLoading = true
        ),
        viewModelScope = null,
        trackRepository = trackRepository,
        sortPreferencesRepository = sortPreferencesRepository,
        args = args
    )

    init {
        trackRepository.getTrackListWithMetaData(args.trackListType)
            .onEach { trackListWithMetadata ->
                val newTrackList = trackListWithMetadata.trackList
                val trackListMetadata = trackListWithMetadata.metaData
                setState {
                    it.copy(
                        modelListState = it.modelListState.copy(
                            items = newTrackList.map { track -> track.toModelListItemState() },
                            headerState = trackListMetadata.toHeaderState()
                        ),
                        isLoading = false
                    )
                }
            }.launchIn(vmScope)

        if (args.trackListType !is MediaGroup.Album) {
            sortPreferencesRepository.getTrackListSortPreferences(args.trackListType)
                .onEach { sortPrefs ->
                    setState {
                        it.copy(
                            modelListState = it.modelListState.copy(
                                sortButtonState = SortButtonState(
                                    text = sortPrefs.sortOption.stringId,
                                    sortOrder = sortPrefs.sortOrder
                                )
                            ),
                        )
                    }
                }.launchIn(vmScope)
        }
    }

    override fun handle(action: TrackListUserAction) = Unit
}

data class TrackListArgumentsForNav(val trackListType: TrackList?) {
    fun toTrackListArguments() =
        TrackListArguments(trackListType ?: MediaGroup.AllTracks)
}

data class TrackListArguments(val trackListType: TrackList)

data class TrackListState(
    val modelListState: ModelListState,
    val trackListType: TrackList,
    val isLoading: Boolean
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackListArgumentsForNavModule {

    @Provides
    @ViewModelScoped
    fun trackListArgumentsForNavProvider(savedStateHandle: SavedStateHandle): TrackListArguments {
        return savedStateHandle.navArgs<TrackListArgumentsForNav>()
            .toTrackListArguments()
    }
}

sealed interface TrackListUserAction : UserAction

fun TrackListState.toUiState(): UiState<TrackListState> {
    return when {
        isLoading -> Loading
        modelListState.items.isEmpty() -> Empty
        else -> Data(this)
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
