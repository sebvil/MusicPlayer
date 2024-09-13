package com.sebastianvm.musicplayer.features.main

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UiComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.features.api.navigation.NavigationHostArguments
import com.sebastianvm.musicplayer.features.api.navigation.navigationFeature
import com.sebastianvm.musicplayer.features.api.player.PlayerArguments
import com.sebastianvm.musicplayer.features.api.player.PlayerProps
import com.sebastianvm.musicplayer.features.api.player.playerFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.registry.create
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    vmScope: CoroutineScope = getViewModelScope(),
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) : BaseViewModel<MainState, MainUserAction>(viewModelScope = vmScope) {

    private val appNavigationHostUiComponent =
        features.navigationFeature().create(arguments = NavigationHostArguments)

    private val _isFullScreen: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val playerUiComponent by lazy {
        features
            .playerFeature()
            .create(
                arguments = PlayerArguments,
                props =
                    _isFullScreen
                        .map {
                            PlayerProps(
                                isFullscreen = it,
                                dismissFullScreenPlayer = { _isFullScreen.value = false },
                            )
                        }
                        .stateIn(
                            viewModelScope,
                            SharingStarted.WhileSubscribed(5_000),
                            PlayerProps(
                                isFullscreen = false,
                                dismissFullScreenPlayer = { _isFullScreen.value = false },
                            ),
                        ),
            )
    }

    override val state: StateFlow<MainState> =
        _isFullScreen
            .map { isFullScreen ->
                MainState(
                    playerMvvmComponent = playerUiComponent,
                    appNavigationHostMvvmComponent = appNavigationHostUiComponent,
                    isFullscreen = isFullScreen,
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
                _isFullScreen.value = true
            }
            is MainUserAction.CollapsePlayer -> {
                _isFullScreen.value = false
            }
        }
    }

    class Factory(
        private val playbackManager: PlaybackManager,
        private val features: FeatureRegistry,
    ) : AbstractSavedStateViewModelFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle,
        ): T {
            return MainViewModel(playbackManager = playbackManager, features = features) as T
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
