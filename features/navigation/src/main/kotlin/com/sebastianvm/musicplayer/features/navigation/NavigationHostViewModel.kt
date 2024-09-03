package com.sebastianvm.musicplayer.features.navigation

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
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
    val mvvmComponent: MvvmComponent<*, *, *>,
    val presentationMode: NavOptions.PresentationMode,
)

data class NavigationState(val backStack: List<BackStackEntry>) : State

sealed interface NavigationAction : UserAction {
    data class ShowScreen(val mvvmComponent: MvvmComponent<*, *, *>, val navOptions: NavOptions) :
        NavigationAction

    data object PopBackStack : NavigationAction
}

class NavigationHostViewModel(
    vmScope: CoroutineScope = getViewModelScope(),
    features: FeatureRegistry,
) : BaseViewModel<NavigationState, NavigationAction>(viewModelScope = vmScope) {

    private val navController =
        object : NavController {
            override fun push(mvvmComponent: MvvmComponent<*, *, *>, navOptions: NavOptions) {
                handle(NavigationAction.ShowScreen(mvvmComponent, navOptions))
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
            .stateIn(viewModelScope, SharingStarted.Lazily, NavigationState(backStack.value))

    override fun handle(action: NavigationAction) {
        when (action) {
            is NavigationAction.ShowScreen -> {
                backStack.update {
                    val entry =
                        BackStackEntry(action.mvvmComponent, action.navOptions.presentationMode)
                    if (action.navOptions.popCurrent) {
                        // TODO clear
                        it.dropLast(1) + entry
                    } else {
                        it + entry
                    }
                }
            }
            NavigationAction.PopBackStack -> {
                backStack.update {
                    val last = it.last()
                    last.mvvmComponent.clear()
                    it.dropLast(1)
                }
            }
        }
    }
}
