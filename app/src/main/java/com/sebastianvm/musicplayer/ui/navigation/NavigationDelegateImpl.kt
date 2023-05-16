package com.sebastianvm.musicplayer.ui.navigation

import androidx.navigation.NavController
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class NavigationDelegateImpl(private val navController: NavController) : NavigationDelegate {

    override fun handleNavEvent(navEvent: NavEvent) {
        when (navEvent) {
            is NavEvent.NavigateUp -> navigateUp()
            is NavEvent.NavigateToScreen -> navigateToScreen(navEvent.destination)
        }
    }

    override fun navigateToScreen(destination: NavigationDestination) {
        navController.navigateTo(destination)
    }

    override fun navigateUp() {
        navController.navigateUp()
    }

    private val _isBottomSheetOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val isBottomSheetOpen: StateFlow<Boolean>
        get() = _isBottomSheetOpen

    override fun openSheet(destination: NavigationDestination) {
        navController.navigateTo(destination)
        _isBottomSheetOpen.update { true }
    }

}