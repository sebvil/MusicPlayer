package com.sebastianvm.musicplayer.ui.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
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
class PlaylistViewModel @Inject constructor(
    initialState: PlaylistState,
    playlistRepository: PlaylistRepository,
) : BaseViewModel<PlaylistUiEvent, PlaylistState>(initialState),
    ViewModelInterface<PlaylistState, PlaylistUserAction> {

    init {
        playlistRepository.getPlaylistName(playlistId = state.value.playlistId)
            .onEach { playlistName ->
                requireNotNull(playlistName)
                setState {
                    copy(
                        playlistName = playlistName,
                    )
                }
            }.launchIn(viewModelScope)
    }

    override fun handle(action: PlaylistUserAction) {
        when (action) {
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
        }
    }

}

data class PlaylistState(
    val playlistId: Long,
    val playlistName: String,
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
        )
    }
}

sealed interface PlaylistUiEvent : UiEvent

sealed interface PlaylistUserAction : UserAction {
    object AddTracksClicked : PlaylistUserAction
    object UpClicked : PlaylistUserAction
    object SortByClicked : PlaylistUserAction
}

