package com.sebastianvm.musicplayer.ui.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemStateWithPosition
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemStateWithPosition
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface
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
class PlaylistViewModel @Inject constructor(
    initialState: PlaylistState,
    playlistRepository: PlaylistRepository,
    private val playbackManager: PlaybackManager,
) : BaseViewModel<PlaylistUiEvent, PlaylistState>(initialState),
    ViewModelInterface<PlaylistState, PlaylistUserAction> {

    init {
        val trackListFlow =
            playlistRepository.getTracksInPlaylist(playlistId = state.value.playlistId)

        combineToPair(
            playlistRepository.getPlaylist(playlistId = state.value.playlistId),
            trackListFlow
        ).onEach { (playlist, tracks) ->
            requireNotNull(playlist)
            setState {
                copy(
                    playlistName = playlist.playlistName,
                    trackList = tracks.map { it.toModelListItemStateWithPosition() }
                )
            }
            addUiEvent(PlaylistUiEvent.ScrollToTop)
        }.launchIn(viewModelScope)
    }

    override fun handle(action: PlaylistUserAction) {
        when (action) {
            is PlaylistUserAction.TrackClicked -> {
                playbackManager.playPlaylist(
                    state.value.playlistId,
                    initialTrackIndex = action.trackIndex
                ).onEach {
                    when (it) {
                        is PlaybackResult.Loading, is PlaybackResult.Error -> setState {
                            copy(
                                playbackResult = it
                            )
                        }
                        is PlaybackResult.Success -> {
                            setState { copy(playbackResult = it) }
                            addNavEvent(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer))
                        }
                    }
                }.launchIn(viewModelScope)
            }
            is PlaylistUserAction.TrackOverflowMenuIconClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            TrackContextMenuArguments(
                                trackId = action.trackId,
                                mediaType = MediaType.TRACK,
                                mediaGroup = MediaGroup(
                                    mediaGroupType = MediaGroupType.PLAYLIST,
                                    mediaId = state.value.playlistId
                                ),
                                trackIndex = action.trackIndex,
                                positionInPlaylist = action.position
                            )
                        )
                    )
                )
            }
            is PlaylistUserAction.AddTracksClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackSearch(
                            TrackSearchArguments(state.value.playlistId)
                        )
                    )
                )
            }
            is PlaylistUserAction.UpClicked -> addNavEvent(NavEvent.NavigateUp)
            is PlaylistUserAction.SortByClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.SortMenu(
                            SortMenuArguments(
                                listType = SortableListType.Playlist,
                                mediaId = state.value.playlistId
                            )
                        )
                    )
                )
            }
            is PlaylistUserAction.DismissPlaybackErrorDialog -> setState { copy(playbackResult = null) }
        }
    }
}

data class PlaylistState(
    val playlistId: Long,
    val playlistName: String,
    val trackList: List<ModelListItemStateWithPosition>,
    val playbackResult: PlaybackResult?
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialPlaylistStateModule {
    @Provides
    @ViewModelScoped
    fun initialPlaylistStateProvider(savedStateHandle: SavedStateHandle): PlaylistState {
        val arguments = savedStateHandle.getArgs<PlaylistArguments>()
        return PlaylistState(
            playlistId = arguments.playlistId,
            playlistName = "",
            trackList = listOf(),
            playbackResult = null
        )
    }
}

sealed interface PlaylistUiEvent : UiEvent {
    object ScrollToTop : PlaylistUiEvent
}

sealed interface PlaylistUserAction : UserAction {
    data class TrackClicked(val trackIndex: Int) : PlaylistUserAction
    data class TrackOverflowMenuIconClicked(
        val trackIndex: Int,
        val trackId: Long,
        val position: Long
    ) :
        PlaylistUserAction

    object AddTracksClicked : PlaylistUserAction
    object UpClicked : PlaylistUserAction
    object SortByClicked : PlaylistUserAction
    object DismissPlaybackErrorDialog : PlaylistUserAction
}