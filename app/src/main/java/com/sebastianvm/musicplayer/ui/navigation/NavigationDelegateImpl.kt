package com.sebastianvm.musicplayer.ui.navigation

import androidx.navigation.NavController
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent

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

}