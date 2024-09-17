package com.sebastianvm.musicplayer.features.navigation

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.home.HomeProps
import com.sebastianvm.musicplayer.features.api.home.home
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.registry.create
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

data class NavigationHostState(val backStack: List<BackStackEntry>) : State

sealed interface NavigationHostUserAction : UserAction {
    data class ShowScreen(val mvvmComponent: MvvmComponent<*, *, *>, val navOptions: NavOptions) :
        NavigationHostUserAction

    data object PopBackStack : NavigationHostUserAction
}

class NavigationHostViewModel(
    viewModelScope: CoroutineScope = getViewModelScope(),
    features: FeatureRegistry,
) : BaseViewModel<NavigationHostState, NavigationHostUserAction>(viewModelScope = viewModelScope) {

    private val navController =
        object : NavController {
            override fun push(mvvmComponent: MvvmComponent<*, *, *>, navOptions: NavOptions) {
                handle(NavigationHostUserAction.ShowScreen(mvvmComponent, navOptions))
            }

            override fun pop() {
                handle(NavigationHostUserAction.PopBackStack)
            }
        }

    private val backStack: MutableStateFlow<List<BackStackEntry>> =
        MutableStateFlow(
            listOf(
                BackStackEntry(
                    features
                        .home()
                        .create(
                            arguments = com.sebastianvm.musicplayer.features.api.home.HomeArguments,
                            props = MutableStateFlow(HomeProps(navController)),
                        ),
                    NavOptions.PresentationMode.Screen,
                )
            )
        )

    override val state: StateFlow<NavigationHostState> =
        backStack
            .map { NavigationHostState(it) }
            .stateIn(viewModelScope, SharingStarted.Lazily, NavigationHostState(backStack.value))

    override fun handle(action: NavigationHostUserAction) {
        when (action) {
            is NavigationHostUserAction.ShowScreen -> {
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
            NavigationHostUserAction.PopBackStack -> {
                backStack.update {
                    val last = it.last()
                    last.mvvmComponent.clear()
                    it.dropLast(1)
                }
            }
        }
    }
}
