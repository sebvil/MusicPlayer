package com.sebastianvm.musicplayer.ui.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
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
    playlistRepository: PlaylistRepository
) : BaseViewModel<PlaylistUiEvent, PlaylistState>(initialState) {

    init {
        playlistRepository.getPlaylist(playlistId = state.value.playlistId)
            .onEach { playlist ->
                requireNotNull(playlist)
                setState {
                    copy(
                        playlistName = playlist.playlistName,
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun onAddTracksClicked() {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.TrackSearch(
                    TrackSearchArguments(state.value.playlistId)
                )
            )
        )
    }


    fun onSortByClicked() {
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


    fun onUpButtonClicked() {
        addNavEvent(NavEvent.NavigateUp)
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

sealed class PlaylistUiEvent : UiEvent