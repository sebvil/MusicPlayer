package com.sebastianvm.musicplayer.ui.navigation

import androidx.navigation.NavOptionsBuilder
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.Direction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent

class NavigationDelegateImpl(private val navigator: DestinationsNavigator) : NavigationDelegate {

    override fun handleNavEvent(navEvent: NavEvent) {
        when (navEvent) {
            is NavEvent.NavigateUp -> navigateUp()
            is NavEvent.NavigateToScreen -> navigateToScreen(navEvent.destination)
        }
    }

    override fun navigateToScreen(
        destination: Direction,
        onlyIfResumed: Boolean,
        builder: NavOptionsBuilder.() -> Unit
    ) {
        navigator.navigate(destination, onlyIfResumed = onlyIfResumed, builder)
    }

    override fun navigateUp() {
        navigator.navigateUp()
    }

}