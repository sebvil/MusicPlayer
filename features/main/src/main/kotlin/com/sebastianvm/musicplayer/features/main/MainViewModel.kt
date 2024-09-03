package com.sebastianvm.musicplayer.features.main

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UiComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.features.api.navigation.navigationFeature
import com.sebastianvm.musicplayer.features.api.player.PlayerDelegate
import com.sebastianvm.musicplayer.features.api.player.PlayerProps
import com.sebastianvm.musicplayer.features.api.player.playerFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MainViewModel(
    vmScope: CoroutineScope = getViewModelScope(),
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) : BaseViewModel<MainState, MainUserAction>(viewModelScope = vmScope) {

    private val appNavigationHostUiComponent = features.navigationFeature().navigationUiComponent()

    private val playerProps: MutableStateFlow<PlayerProps> =
        MutableStateFlow(PlayerProps(isFullscreen = false))

    private val playerUiComponent =
        features
            .playerFeature()
            .playerUiComponent(
                props = playerProps,
                delegate =
                    object : PlayerDelegate {
                        override fun dismissFullScreenPlayer() {
                            playerProps.update { it.copy(isFullscreen = false) }
                        }
                    },
            )

    override val state: StateFlow<MainState> =
        playerProps
            .map { props ->
                MainState(
                    playerMvvmComponent = playerUiComponent,
                    appNavigationHostMvvmComponent = appNavigationHostUiComponent,
                    isFullscreen = props.isFullscreen,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue =
                    MainState(
                        playerMvvmComponent = playerUiComponent,
                        appNavigationHostMvvmComponent = appNavigationHostUiComponent,
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

    class Factory(private val services: Services) : AbstractSavedStateViewModelFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle,
        ): T {
            return MainViewModel(
                playbackManager = services.playbackManager,
                features = services.featureRegistry,
            )
                as T
        }
    }
}

data class MainState(
    val playerMvvmComponent: UiComponent,
    val appNavigationHostMvvmComponent: UiComponent,
    val isFullscreen: Boolean,
) : State

sealed interface MainUserAction : UserAction {
    data object ConnectToMusicService : MainUserAction

    data object DisconnectFromMusicService : MainUserAction

    data object ExpandPlayer : MainUserAction

    data object CollapsePlayer : MainUserAction
}
