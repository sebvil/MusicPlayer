package com.sebastianvm.musicplayer.ui.queue

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@HiltViewModel
class QueueViewModel @Inject constructor(
    initialState: QueueState,
    musicServiceConnection: MusicServiceConnection
) : BaseViewModel<QueueUserAction, QueueUiEvent, QueueState>(initialState) {

    init {

    }

    override fun handle(action: QueueUserAction) = Unit
}

data class QueueState(
    val queueItems: List<TrackRowState>
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialQueueStateModule {
    @Provides
    @ViewModelScoped
    fun initialQueueStateProvider(savedStateHandle: SavedStateHandle): QueueState {
        return QueueState(queueItems = listOf())
    }
}

sealed class QueueUserAction : UserAction
sealed class QueueUiEvent : UiEvent

