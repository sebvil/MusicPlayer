package com.sebastianvm.musicplayer

import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.State
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
    private val mediaPlaybackRepository: MediaPlaybackRepository
) :
    BaseViewModel<MainActivityUiEvent, MainActivityState>(initialState) {
    fun <A: UserAction> handle(action: A) {
        when (action) {
            is MainActivityUserAction.ConnectToMusicService -> {
                mediaPlaybackRepository.connectToService()
            }
            is MainActivityUserAction.DisconnectFromMusicService -> {
                mediaPlaybackRepository.disconnectFromService()
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
