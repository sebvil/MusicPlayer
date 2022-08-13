package com.sebastianvm.musicplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import kotlinx.coroutines.flow.map

class NavigationDelegate(private val navController: NavController) {

    fun handleNavEvent(navEvent: NavEvent) {
        when (navEvent) {
            is NavEvent.NavigateUp -> navigateUp()
            is NavEvent.NavigateToScreen -> navigateToScreen(navEvent.destination)
        }
    }


    @Composable
    fun isRouteInGraphAsState(navigationRoute: NavigationRoute): State<Boolean> {
        return navController.currentBackStackEntryFlow.map { backStackEntry ->
            val currentDestination = backStackEntry.destination
            currentDestination.hierarchy.any { it.route == navigationRoute.name }
        }.collectAsState(initial = false)
    }

    private fun navigateToScreen(destination: NavigationDestination) {
        if (!destination.isBottomNavDestination) {
            navController.navigateTo(destination)
        } else {
            navController.navigateTo(destination) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
        }
    }

    private fun navigateUp() {
        navController.navigateUp()
    }

}