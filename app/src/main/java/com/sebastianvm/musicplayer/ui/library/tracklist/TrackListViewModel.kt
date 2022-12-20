package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.playlist.TrackSearchArguments
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.coroutines.combineToPair
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
) : BaseViewModel<TrackListState, TrackListUserAction, TrackListUiEvent>(initialState) {

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
                addUiEvent(TrackListUiEvent.ScrollToTop)
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
                            addNavEvent(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer))
                        }
                    }
                }.launchIn(viewModelScope)

            }

            is TrackListUserAction.TrackOverflowMenuIconClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            TrackContextMenuArguments(
                                trackId = action.trackId,
                                mediaGroup = state.trackListType,
                                trackIndex = action.trackIndex,
                                positionInPlaylist = action.position
                            )
                        )
                    )
                )
            }

            is TrackListUserAction.UpButtonClicked -> addNavEvent(NavEvent.NavigateUp)
            is TrackListUserAction.SortByButtonClicked -> {
                val trackListType = state.trackListType
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.SortMenu(
                            SortMenuArguments(
                                listType = when (trackListType) {
                                    is MediaGroup.AllTracks -> SortableListType.Tracks(trackList = trackListType)
                                    is MediaGroup.Genre -> SortableListType.Tracks(trackList = trackListType)
                                    is MediaGroup.Playlist -> SortableListType.Playlist(playlistId = trackListType.playlistId)
                                    is MediaGroup.Album -> throw IllegalStateException("Cannot sort album")
                                }
                            )
                        )
                    )
                )
            }

            is TrackListUserAction.AddTracksClicked -> {
                val trackListType = state.trackListType
                check(trackListType is MediaGroup.Playlist)
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackSearch(
                            TrackSearchArguments(trackListType.playlistId)
                        )
                    )
                )
            }
        }
    }

}

data class TrackListState(
    val trackListType: TrackList,
    val trackList: List<ModelListItemState>,
    val trackListName: String? = null,
    val playbackResult: PlaybackResult? = null,
    val headerImage: MediaArtImageState? = null
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackListStateModule {

    @Provides
    @ViewModelScoped
    fun initialTrackListStateProvider(savedStateHandle: SavedStateHandle): TrackListState {
        val args = savedStateHandle.getArgs<TrackListArguments>()
        return TrackListState(
            trackList = listOf(),
            trackListType = args.trackList,
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
    object UpButtonClicked : TrackListUserAction
    object SortByButtonClicked : TrackListUserAction
    object AddTracksClicked : TrackListUserAction
}