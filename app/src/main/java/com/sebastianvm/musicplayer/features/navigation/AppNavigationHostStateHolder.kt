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

data class BackStackEntry(
    val screen: Screen<*, *>,
    val presentationMode: NavOptions.PresentationMode
)

data class AppNavigationState(val backStack: List<BackStackEntry>) : State

sealed interface AppNavigationAction : UserAction {
    data class ShowScreen(val screen: Screen<*, *>, val navOptions: NavOptions) :
        AppNavigationAction

    data object PopBackStack : AppNavigationAction
}

class AppNavigationHostStateHolder(stateHolderScope: CoroutineScope = stateHolderScope()) :
    StateHolder<AppNavigationState, AppNavigationAction> {

    private val navController = object : NavController {
        override fun push(screen: Screen<*, *>, navOptions: NavOptions) {
            handle(AppNavigationAction.ShowScreen(screen, navOptions))
        }

        override fun pop() {
            handle(AppNavigationAction.PopBackStack)
        }
    }

    private val backStack: MutableStateFlow<List<BackStackEntry>> = MutableStateFlow(
        listOf(
            BackStackEntry(MainScreen(navController), NavOptions.PresentationMode.Screen)
        )
    )

    override val state: StateFlow<AppNavigationState> = backStack.map {
        AppNavigationState(it)
    }.stateIn(stateHolderScope, SharingStarted.Lazily, AppNavigationState(backStack.value))

    override fun handle(action: AppNavigationAction) {
        when (action) {
            is AppNavigationAction.ShowScreen -> {
                backStack.update {
                    val entry = BackStackEntry(action.screen, action.navOptions.presentationMode)
                    if (action.navOptions.popCurrent) {
                        it.dropLast(1) + entry
                    } else {
                        it + entry
                    }
                }
            }

            AppNavigationAction.PopBackStack -> {
                backStack.update {
                    val last = it.last()
                    last.screen.onCleared()
                    it.dropLast(1)
                }
            }
        }
    }
}
