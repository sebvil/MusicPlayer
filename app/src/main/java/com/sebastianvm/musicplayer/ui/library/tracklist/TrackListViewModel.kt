package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.navArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.util.coroutines.combineToPair
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
) : BaseViewModel<TrackListState, TrackListUserAction>(initialState) {

    init {
        with(trackRepository) {
            combineToPair(
                getTracksForMedia(state.trackListType),
                getTrackListMetadata(state.trackListType)
            ).onEach { (newTrackList, trackListMetadata) ->
                setState {
                    copy(
                        trackList = newTrackList,
                        trackListName = trackListMetadata.trackListName,
                        headerImage = trackListMetadata.mediaArtImageState
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun handle(action: TrackListUserAction) {
        when (action) {
            is TrackListUserAction.DismissPlaybackErrorDialog -> setState { copy(playbackResult = null) }
            is TrackListUserAction.TrackClicked -> {
                val playTracksFlow = playbackManager.playMedia(
                    mediaGroup = state.trackListType,
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
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

}

data class TrackListArguments(val trackList: TrackList?)


data class TrackListState(
    val trackListType: TrackList,
    val trackList: List<ModelListItemState>,
    val trackListName: String? = null,
    val playbackResult: PlaybackResult? = null,
    val headerImage: MediaArtImageState? = null,
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackListStateModule {

    @Provides
    @ViewModelScoped
    fun initialTrackListStateProvider(savedStateHandle: SavedStateHandle): TrackListState {
        val args: TrackListArguments = savedStateHandle.navArgs()
        return TrackListState(
            trackList = listOf(),
            trackListType = args.trackList ?: MediaGroup.AllTracks,
        )
    }
}


sealed interface TrackListUserAction : UserAction {
    data class TrackClicked(val trackIndex: Int) : TrackListUserAction
    object DismissPlaybackErrorDialog : TrackListUserAction
}