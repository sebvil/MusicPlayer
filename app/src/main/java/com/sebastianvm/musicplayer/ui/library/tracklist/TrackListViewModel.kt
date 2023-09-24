package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.OldBaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TrackListViewModel @Inject constructor(
    trackRepository: TrackRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    private val args: TrackListArguments,
) : OldBaseViewModel<TrackListState, TrackListUserAction>() {

    init {
        trackRepository.getTrackListWithMetaData(args.trackListType)
            .onEach { trackListWithMetadata ->
                val newTrackList = trackListWithMetadata.trackList
                val trackListMetadata = trackListWithMetadata.metaData
                if (newTrackList.isEmpty()) {
                    setState { Empty }
                } else {
                    setDataState {
                        it.copy(
                            modelListState = it.modelListState.copy(
                                items = newTrackList.map { track -> track.toModelListItemState() },
                                headerState = when {
                                    trackListMetadata == null -> HeaderState.None
                                    trackListMetadata.mediaArtImageState != null -> {
                                        HeaderState.WithImage(
                                            title = trackListMetadata.trackListName,
                                            trackListMetadata.mediaArtImageState
                                        )
                                    }

                                    else -> HeaderState.Simple(title = trackListMetadata.trackListName)
                                }
                            ),
                            trackListType = args.trackListType
                        )
                    }
                }
            }.launchIn(viewModelScope)

        sortPreferencesRepository.getTrackListSortPreferences(args.trackListType)
            .onEach { sortPrefs ->
                setDataState {
                    it.copy(
                        modelListState = it.modelListState.copy(
                            sortButtonState = SortButtonState(
                                text = sortPrefs.sortOption.stringId,
                                sortOrder = sortPrefs.sortOrder
                            )
                        ),
                    )
                }
            }.launchIn(viewModelScope)
    }

    override fun handle(action: TrackListUserAction) = Unit

    override val defaultState: TrackListState by lazy {
        TrackListState(
            trackListType = args.trackListType,
            modelListState = ModelListState(
                items = listOf(),
                sortButtonState = null,
                headerState = HeaderState.None
            ),
        )
    }
}

data class TrackListArgumentsForNav(val trackListType: TrackList?) {
    fun toTrackListArguments() =
        TrackListArguments(trackListType ?: MediaGroup.AllTracks)
}

data class TrackListArguments(val trackListType: TrackList)

data class TrackListState(
    val modelListState: ModelListState,
    val trackListType: TrackList,
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
