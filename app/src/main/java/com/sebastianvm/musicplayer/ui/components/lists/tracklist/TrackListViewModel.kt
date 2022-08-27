package com.sebastianvm.musicplayer.ui.components.lists.tracklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class TrackListViewModel @Inject constructor(
    initialState: TrackListState,
    trackRepository: TrackRepository,
    private val playbackManager: PlaybackManager,
) : BaseViewModel<TrackListUiEvent, TrackListState>(initialState),
    ViewModelInterface<TrackListState, TrackListUserAction> {

    init {
        trackRepository.getTracksForMedia(state.value.trackListType, state.value.trackListId)
            .onEach { newTrackList ->
                setState { copy(trackList = newTrackList) }
                addUiEvent(TrackListUiEvent.ScrollToTop)
            }.launchIn(viewModelScope)

    }

    override fun handle(action: TrackListUserAction) {
        when (action) {
            is TrackListUserAction.DismissPlaybackErrorDialog -> setState { copy(playbackResult = null) }

            is TrackListUserAction.TrackClicked -> {
                val playTracksFlow = playbackManager.playMedia(
                    mediaGroup = MediaGroup(
                        state.value.trackListType.toMediaGroupType(),
                        state.value.trackListId
                    ),
                    initialTrackIndex = action.trackIndex
                )
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
                    mediaGroupType = state.value.trackListType.toMediaGroupType(),
                    mediaId = state.value.trackListId
                )
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            TrackContextMenuArguments(
                                trackId = action.trackId,
                                mediaType = MediaType.TRACK,
                                mediaGroup = mediaGroup,
                                trackIndex = action.trackIndex,
                                positionInPlaylist = action.position
                            )
                        )
                    )
                )
            }
        }
    }

}


data class TrackListState(
    val trackListId: Long,
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
        val args = savedStateHandle.getArgs<HasTrackList>().args
        return TrackListState(
            trackListId = args.trackListId,
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
    data class TrackOverflowMenuIconClicked(
        val trackIndex: Int,
        val trackId: Long,
        val position: Long? = null
    ) : TrackListUserAction

    object DismissPlaybackErrorDialog : TrackListUserAction
}