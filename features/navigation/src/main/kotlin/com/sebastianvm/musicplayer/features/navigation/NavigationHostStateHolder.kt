package com.sebastianvm.musicplayer.features.navigation

import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.stateHolderScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.home.home
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class BackStackEntry(
    val uiComponent: UiComponent<*>,
    val presentationMode: NavOptions.PresentationMode,
)

data class NavigationState(val backStack: List<BackStackEntry>) : State

sealed interface NavigationAction : UserAction {
    data class ShowScreen(val uiComponent: UiComponent<*>, val navOptions: NavOptions) :
        NavigationAction

    data object PopBackStack : NavigationAction
}

class NavigationHostStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    features: FeatureRegistry,
) : StateHolder<NavigationState, NavigationAction> {

    private val navController =
        object : NavController {
            override fun push(uiComponent: UiComponent<*>, navOptions: NavOptions) {
                handle(NavigationAction.ShowScreen(uiComponent, navOptions))
            }

            override fun pop() {
                handle(NavigationAction.PopBackStack)
            }
        }

    private val backStack: MutableStateFlow<List<BackStackEntry>> =
        MutableStateFlow(
            listOf(
                BackStackEntry(
                    features.home().homeUiComponent(navController = navController),
                    NavOptions.PresentationMode.Screen,
                )
            )
        )

    override val state: StateFlow<NavigationState> =
        backStack
            .map { NavigationState(it) }
            .stateIn(stateHolderScope, SharingStarted.Lazily, NavigationState(backStack.value))

    override fun handle(action: NavigationAction) {
        when (action) {
            is NavigationAction.ShowScreen -> {
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
            NavigationAction.PopBackStack -> {
                backStack.update {
                    val last = it.last()
                    last.uiComponent.onCleared()
                    it.dropLast(1)
                }
            }
        }
    }
}
