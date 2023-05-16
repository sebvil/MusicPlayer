package com.sebastianvm.musicplayer.ui.navigation

import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import kotlinx.coroutines.flow.StateFlow

interface NavigationDelegate {

    fun handleNavEvent(navEvent: NavEvent)

    fun navigateToScreen(destination: NavigationDestination)
    fun navigateUp()

    val isBottomSheetOpen: StateFlow<Boolean>

    fun openSheet(destination: NavigationDestination)

}