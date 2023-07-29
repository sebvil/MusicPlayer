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
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
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
    trackRepository: TrackRepository,
    private val args: TrackListArguments,
    private val playbackManager: PlaybackManager,
) : BaseViewModel<TrackListState, TrackListUserAction>() {

    init {
        with(trackRepository) {
            combineToPair(
                getTracksForMedia(args.trackListType),
                getTrackListMetadata(args.trackListType)
            ).onEach { (newTrackList, trackListMetadata) ->
                if (newTrackList.isEmpty()) {
                    setState { Empty }
                } else {
                    setDataState {
                        it.copy(
                            trackList = newTrackList,
                            trackListName = trackListMetadata.trackListName,
                            headerImage = trackListMetadata.mediaArtImageState
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun handle(action: TrackListUserAction) {
        when (action) {
            is TrackListUserAction.DismissPlaybackErrorDialog -> {
                setDataState {
                    it.copy(
                        playbackResult = null
                    )
                }
            }

            is TrackListUserAction.TrackClicked -> {
                val playTracksFlow = playbackManager.playMedia(
                    mediaGroup = args.trackListType,
                    initialTrackIndex = action.trackIndex
                )
                playTracksFlow.onEach { result ->
                    when (result) {
                        is PlaybackResult.Loading, is PlaybackResult.Error -> {
                            setDataState {
                                it.copy(
                                    playbackResult = result
                                )
                            }
                        }

                        is PlaybackResult.Success -> {
                            setDataState {
                                it.copy(
                                    playbackResult = null
                                )
                            }
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }


    override val defaultState: TrackListState by lazy {
        TrackListState(
            trackListType = args.trackListType,
            trackList = listOf(),
            trackListName = null,
            playbackResult = null,
            headerImage = null
        )
    }
}

data class TrackListArgumentsForNav(val trackListType: TrackList?) {
    fun toTrackListArguments() =
        TrackListArguments(trackListType ?: MediaGroup.AllTracks)
}

data class TrackListArguments(val trackListType: TrackList)


data class TrackListState(
    val trackListType: TrackList,
    val trackList: List<ModelListItemState>,
    val trackListName: String? = null,
    val playbackResult: PlaybackResult? = null,
    val headerImage: MediaArtImageState? = null,
)


@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackListArgumentsForNavModule {

    @Provides
    @ViewModelScoped
    fun initialTrackListArgumentsForNavProvider(savedStateHandle: SavedStateHandle): TrackListArguments {
        return savedStateHandle.navArgs<TrackListArgumentsForNav>()
            .toTrackListArguments()
    }
}


sealed interface TrackListUserAction : UserAction {
    data class TrackClicked(val trackIndex: Int) : TrackListUserAction
    data object DismissPlaybackErrorDialog : TrackListUserAction
}