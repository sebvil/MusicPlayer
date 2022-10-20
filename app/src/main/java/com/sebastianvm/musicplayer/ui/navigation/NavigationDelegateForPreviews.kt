package com.sebastianvm.musicplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent


class NavigationDelegateForPreviews : NavigationDelegate {
    override fun handleNavEvent(navEvent: NavEvent) = Unit

    @Composable
    override fun isRouteInGraphAsState(navigationRoute: NavigationRoute): State<Boolean> {
        return remember {
            mutableStateOf(true)
        }
    }
}