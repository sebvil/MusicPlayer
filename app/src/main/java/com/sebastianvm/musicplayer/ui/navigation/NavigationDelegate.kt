package com.sebastianvm.musicplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent

interface NavigationDelegate {

    fun handleNavEvent(navEvent: NavEvent)

    fun navigateToScreen(destination: NavigationDestination)
    @Composable
    fun isRouteInGraphAsState(navigationRoute: NavigationRoute): State<Boolean>

}