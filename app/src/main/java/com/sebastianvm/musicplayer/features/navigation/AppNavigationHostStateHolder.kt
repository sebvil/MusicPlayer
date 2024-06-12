package com.sebastianvm.musicplayer.features.navigation

import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.sebastianvm.musicplayer.features.home.HomeUiComponent
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import com.sebastianvm.musicplayer.util.extensions.collectValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class BackStackEntry(
    val uiComponent: UiComponent<*, *>,
    val presentationMode: NavOptions.PresentationMode,
)

data class AppNavigationState(val backStack: List<BackStackEntry>) : State

sealed interface AppNavigationAction : UserAction {
    data class ShowScreen(val uiComponent: UiComponent<*, *>, val navOptions: NavOptions) :
        AppNavigationAction

    data object PopBackStack : AppNavigationAction
}

class AppNavigationHostStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    recompositionMode: RecompositionMode = RecompositionMode.ContextClock,
) : StateHolder<AppNavigationState, AppNavigationAction> {

    private val navController =
        object : NavController {
            override fun push(uiComponent: UiComponent<*, *>, navOptions: NavOptions) {
                handle(AppNavigationAction.ShowScreen(uiComponent, navOptions))
            }

            override fun pop() {
                handle(AppNavigationAction.PopBackStack)
            }
        }

    private val backStack: MutableStateFlow<List<BackStackEntry>> =
        MutableStateFlow(
            listOf(
                BackStackEntry(HomeUiComponent(navController), NavOptions.PresentationMode.Screen)
            )
        )

    override val state: StateFlow<AppNavigationState> =
        stateHolderScope.launchMolecule(recompositionMode) {
            AppNavigationState(backStack = backStack.collectValue())
        }

    override fun handle(action: AppNavigationAction) {
        when (action) {
            is AppNavigationAction.ShowScreen -> {
                backStack.update {
                    val entry =
                        BackStackEntry(action.uiComponent, action.navOptions.presentationMode)
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
                    last.uiComponent.onCleared()
                    it.dropLast(1)
                }
            }
        }
    }
}
