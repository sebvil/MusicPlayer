package com.sebastianvm.musicplayer.ui.navigation

import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent

interface NavigationDelegate {

    fun handleNavEvent(navEvent: NavEvent)

    fun navigateToScreen(destination: NavigationDestination)
    fun navigateUp()

}