package com.sebastianvm.musicplayer.ui.playlist

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.ui.components.TrackRowState
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
import javax.inject.Inject


@HiltViewModel
class PlaylistViewModel @Inject constructor(initialState: PlaylistState) :
    BaseViewModel<PlaylistUiEvent, PlaylistState>(initialState)

data class PlaylistState(
    val playlistId: Long,
    val playlistName: String,
    val trackList: List<TrackRowState>
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
            trackList = listOf()
        )
    }
}

sealed class PlaylistUiEvent : UiEvent

