package com.sebastianvm.musicplayer

import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    initialState: MainActivityState,
    private val playbackManager: PlaybackManager
) : BaseViewModel<MainActivityUiEvent, MainActivityState>(initialState),
    ViewModelInterface<MainActivityState, MainActivityUserAction> {
    override fun handle(action: MainActivityUserAction) {
        when (action) {
            is MainActivityUserAction.ConnectToMusicService -> {
                playbackManager.connectToService()
            }
            is MainActivityUserAction.DisconnectFromMusicService -> {
                playbackManager.disconnectFromService()
            }
        }
    }
}


object MainActivityState : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialMainActivityStateModule {
    @Provides
    @ViewModelScoped
    fun initialMainActivityStateProvider(): MainActivityState = MainActivityState
}

sealed class MainActivityUserAction : UserAction {
    object ConnectToMusicService : MainActivityUserAction()
    object DisconnectFromMusicService : MainActivityUserAction()
}

sealed class MainActivityUiEvent : UiEvent
