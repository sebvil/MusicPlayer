package com.sebastianvm.musicplayer.ui.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
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
    initialState: PlaylistState, playlistRepository: PlaylistRepository
) : BaseViewModel<PlaylistUiEvent, PlaylistState>(initialState) {

    init {
        playlistRepository.getPlaylistWithTracks(state.value.playlistId).onEach {
            setState {
                copy(playlistName = it.playlist.playlistName,
                    trackList = it.tracks.map { track -> track.toTrackRowState(includeTrackNumber = false) })
            }
        }.launchIn(viewModelScope)
    }

    fun onAddTracksClicked() {
        addUiEvent(
            PlaylistUiEvent.NavEvent(
                NavigationDestination.TrackSearchDestination(
                    TrackSearchArguments(state.value.playlistId)
                )
            )
        )
    }
}

data class PlaylistState(
    val playlistId: Long, val playlistName: String, val trackList: List<TrackRowState>
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialPlaylistStateModule {
    @Provides
    @ViewModelScoped
    fun initialPlaylistStateProvider(savedStateHandle: SavedStateHandle): PlaylistState {
        val arguments = savedStateHandle.getArgs<PlaylistArguments>()
        return PlaylistState(
            playlistId = arguments.playlistId, playlistName = "", trackList = listOf()
        )
    }
}

sealed class PlaylistUiEvent : UiEvent {
    data class NavEvent(val navigationDestination: NavigationDestination) : PlaylistUiEvent()
}

