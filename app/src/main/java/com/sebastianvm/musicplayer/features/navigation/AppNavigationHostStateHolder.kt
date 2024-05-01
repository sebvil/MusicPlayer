package com.sebastianvm.musicplayer.features.navigation

import com.sebastianvm.musicplayer.features.main.MainScreen
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AppNavigationState(val backStack: List<Screen<*>>) : State

sealed interface AppNavigationAction : UserAction {
    data class ShowScreen(val screen: Screen<*>) : AppNavigationAction
    data object PopBackStack : AppNavigationAction
}

class AppNavigationHostStateHolder : StateHolder<AppNavigationState, AppNavigationAction> {

    private val _state = MutableStateFlow(AppNavigationState(listOf(MainScreen)))

    override val state: StateFlow<AppNavigationState>
        get() = _state.asStateFlow()

    override fun handle(action: AppNavigationAction) {
        when (action) {
            is AppNavigationAction.ShowScreen -> {
                _state.update {
                    it.copy(
                        backStack = it.backStack + action.screen
                    )
                }
            }

            AppNavigationAction.PopBackStack -> {
                _state.update {
                    it.copy(
                        backStack = it.backStack.dropLast(1)
                    )
                }
            }
        }
    }
}
