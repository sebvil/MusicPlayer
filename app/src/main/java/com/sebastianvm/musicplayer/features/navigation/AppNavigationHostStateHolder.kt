package com.sebastianvm.musicplayer.features.navigation

import com.sebastianvm.musicplayer.features.main.MainScreen
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class AppNavigationState(val backStack: List<Screen<*>>) : State

sealed interface AppNavigationAction : UserAction {
    data class ShowScreen(val screen: Screen<*>) : AppNavigationAction
    data object PopBackStack : AppNavigationAction
}

class AppNavigationHostStateHolder(stateHolderScope: CoroutineScope = stateHolderScope()) :
    StateHolder<AppNavigationState, AppNavigationAction> {

    private val navController = object : NavController {
        override fun push(screen: Screen<*>) {
            handle(AppNavigationAction.ShowScreen(screen))
        }

        override fun pop() {
            handle(AppNavigationAction.PopBackStack)
        }
    }

    private val backStack: MutableStateFlow<List<Screen<*>>> = MutableStateFlow(
        listOf(
            MainScreen(navController)
        )
    )

    override val state: StateFlow<AppNavigationState> = backStack.map {
        AppNavigationState(it)
    }.stateIn(stateHolderScope, SharingStarted.Lazily, AppNavigationState(backStack.value))

    override fun handle(action: AppNavigationAction) {
        when (action) {
            is AppNavigationAction.ShowScreen -> {
                backStack.update {
                    it + action.screen
                }
            }

            AppNavigationAction.PopBackStack -> {
                backStack.update {
                    it.dropLast(1)
                }
            }
        }
    }
}
