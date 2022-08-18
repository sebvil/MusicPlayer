package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.extensions.getArgs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class TrackListViewModel @Inject constructor(
    initialState: TrackListState,
    trackRepository: TrackRepository,
    genreRepository: GenreRepository,
    private val playbackManager: PlaybackManager,
) : BaseViewModel<TrackListUiEvent, TrackListState>(initialState),
    ViewModelInterface<TrackListState, TrackListUserAction> {

    init {
        val trackListFlow = when (state.value.trackListType) {
            TrackListType.ALL_TRACKS -> trackRepository.getAllTracks()
            TrackListType.GENRE -> trackRepository.getTracksForGenre(state.value.trackListId)
        }

        val listNameFlow: Flow<String?> = when (state.value.trackListType) {
            TrackListType.ALL_TRACKS -> flowOf(null)
            TrackListType.GENRE -> genreRepository.getGenre(state.value.trackListId)
                .map { it.genreName }
        }


        combine(trackListFlow, listNameFlow) { tracks, listName ->
            Pair(tracks, listName)
        }.onEach { (newTrackList, listName) ->
            setState {
                copy(
                    trackListName = listName,
                    trackList = newTrackList.map { it.toModelListItemState() },
                )
            }
            addUiEvent(TrackListUiEvent.ScrollToTop)
        }.launchIn(viewModelScope)

    }

    override fun handle(action: TrackListUserAction) {
        when (action) {
            is TrackListUserAction.DismissPlaybackErrorDialog -> setState { copy(playbackResult = null) }

            is TrackListUserAction.TrackClicked -> {
                val playTracksFlow = when (state.value.trackListType) {
                    TrackListType.ALL_TRACKS -> {
                        playbackManager.playAllTracks(initialTrackIndex = action.trackIndex)
                    }
                    TrackListType.GENRE -> {
                        playbackManager.playGenre(
                            state.value.trackListId,
                            initialTrackIndex = action.trackIndex,
                        )
                    }
                }
                playTracksFlow.onEach {
                    when (it) {
                        is PlaybackResult.Loading, is PlaybackResult.Error -> setState {
                            copy(
                                playbackResult = it
                            )
                        }
                        is PlaybackResult.Success -> {
                            setState { copy(playbackResult = null) }
                            addNavEvent(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer))
                        }
                    }
                }.launchIn(viewModelScope)

            }
            is TrackListUserAction.TrackOverflowMenuIconClicked -> {
                val mediaGroup = MediaGroup(
                    mediaGroupType = when (state.value.trackListType) {
                        TrackListType.ALL_TRACKS -> MediaGroupType.ALL_TRACKS
                        TrackListType.GENRE -> MediaGroupType.GENRE
                    },
                    mediaId = state.value.trackListId
                )
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            TrackContextMenuArguments(
                                trackId = action.trackId,
                                mediaType = MediaType.TRACK,
                                mediaGroup = mediaGroup,
                                trackIndex = action.trackIndex
                            )
                        )
                    )
                )
            }
            is TrackListUserAction.UpButtonClicked -> addNavEvent(NavEvent.NavigateUp)
            is TrackListUserAction.SortByButtonClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.SortMenu(
                            SortMenuArguments(
                                listType = SortableListType.Tracks(state.value.trackListType),
                                mediaId = state.value.trackListId
                            )
                        )
                    )
                )
            }
        }
    }

    companion object {
        const val ALL_TRACKS = 0L
    }
}


data class TrackListState(
    val trackListId: Long,
    val trackListName: String?,
    val trackListType: TrackListType,
    val trackList: List<ModelListItemState>,
    val playbackResult: PlaybackResult? = null
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackListStateModule {

    @Provides
    @ViewModelScoped
    fun initialTrackListStateProvider(savedStateHandle: SavedStateHandle): TrackListState {
        val args = savedStateHandle.getArgs<TrackListArguments>()
        return TrackListState(
            trackListId = args.trackListId,
            trackListName = null,
            trackList = listOf(),
            trackListType = args.trackListType,
        )
    }
}

sealed class TrackListUiEvent : UiEvent {
    object ScrollToTop : TrackListUiEvent()
}

sealed interface TrackListUserAction : UserAction {
    data class TrackClicked(val trackIndex: Int) : TrackListUserAction
    data class TrackOverflowMenuIconClicked(val trackIndex: Int, val trackId: Long) :
        TrackListUserAction

    object UpButtonClicked : TrackListUserAction
    object DismissPlaybackErrorDialog : TrackListUserAction
    object SortByButtonClicked : TrackListUserAction
}