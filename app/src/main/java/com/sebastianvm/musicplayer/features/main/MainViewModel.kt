package com.sebastianvm.musicplayer.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.playback.PlaybackManager
import com.sebastianvm.musicplayer.features.navigation.AppNavigationHostUiComponent
import com.sebastianvm.musicplayer.features.player.PlayerDelegate
import com.sebastianvm.musicplayer.features.player.PlayerProps
import com.sebastianvm.musicplayer.features.player.PlayerUiComponent
import com.sebastianvm.musicplayer.ui.util.CloseableCoroutineScope
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MainViewModel(
    override val stateHolderScope: CloseableCoroutineScope = stateHolderScope(),
    private val playbackManager: PlaybackManager,
) : StateHolder<MainState, MainUserAction>, ViewModel(viewModelScope = stateHolderScope) {

    private val appNavigationHostUiComponent = AppNavigationHostUiComponent()

    private val playerProps: MutableStateFlow<PlayerProps> =
        MutableStateFlow(PlayerProps(isFullscreen = false))

    private val playerUiComponent =
        PlayerUiComponent(
            delegate =
                object : PlayerDelegate {
                    override fun dismissFullScreenPlayer() {
                        playerProps.update { it.copy(isFullscreen = false) }
                    }
                },
            props = playerProps,
        )

    override val state: StateFlow<MainState> =
        playerProps
            .map { props ->
                MainState(
                    playerUiComponent = playerUiComponent,
                    appNavigationHostUiComponent = appNavigationHostUiComponent,
                    isFullscreen = props.isFullscreen,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue =
                    MainState(
                        playerUiComponent = playerUiComponent,
                        appNavigationHostUiComponent = appNavigationHostUiComponent,
                        isFullscreen = false,
                    ),
            )

    override fun handle(action: MainUserAction) {
        when (action) {
            is MainUserAction.ConnectToMusicService -> {
                playbackManager.connectToService()
            }
            is MainUserAction.DisconnectFromMusicService -> {
                playbackManager.disconnectFromService()
            }
            is MainUserAction.ExpandPlayer -> {
                playerProps.update { it.copy(isFullscreen = true) }
            }
            is MainUserAction.CollapsePlayer -> {
                playerProps.update { it.copy(isFullscreen = false) }
            }
        }
    }
}

data class MainState(
    val playerUiComponent: PlayerUiComponent,
    val appNavigationHostUiComponent: AppNavigationHostUiComponent,
    val isFullscreen: Boolean,
) : State

sealed interface MainUserAction : UserAction {
    data object ConnectToMusicService : MainUserAction

    data object DisconnectFromMusicService : MainUserAction

    data object ExpandPlayer : MainUserAction

    data object CollapsePlayer : MainUserAction
}
