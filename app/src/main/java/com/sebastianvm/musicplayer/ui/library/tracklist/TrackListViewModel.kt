package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.navArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TrackListViewModel @Inject constructor(
    trackRepository: TrackRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    private val args: TrackListArguments,
    private val playbackManager: PlaybackManager
) : BaseViewModel<TrackListState, TrackListUserAction>() {

    init {
        with(trackRepository) {
            combine(
                getTracksForMedia(args.trackListType),
                getTrackListMetadata(args.trackListType),
                if (args.trackListType !is MediaGroup.Album) {
                    sortPreferencesRepository.getTrackListSortPreferences(
                        args.trackListType
                    )
                } else {
                    flowOf(null)
                }

            ) { newTrackList, trackListMetadata, sortPrefs ->
                Triple(newTrackList, trackListMetadata, sortPrefs)
            }.onEach { (newTrackList, trackListMetadata, sortPrefs) ->
                if (newTrackList.isEmpty()) {
                    setState { Empty }
                } else {
                    setDataState {
                        it.copy(
                            modelListState = ModelListState(
                                items = newTrackList,
                                sortButtonState = sortPrefs?.let {
                                    SortButtonState(
                                        text = sortPrefs.sortOption.stringId,
                                        sortOrder = sortPrefs.sortOrder
                                    )
                                },
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
            modelListState = ModelListState(
                items = listOf(),
                sortButtonState = null,
                headerState = HeaderState.None
            ),
            playbackResult = null
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
    val playbackResult: PlaybackResult? = null
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

sealed interface TrackListUserAction : UserAction {
    data class TrackClicked(val trackIndex: Int) : TrackListUserAction
    data object DismissPlaybackErrorDialog : TrackListUserAction
}
