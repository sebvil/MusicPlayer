package com.sebastianvm.musicplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class NavigationDelegateImpl(private val navController: NavController) : NavigationDelegate {

    override fun handleNavEvent(navEvent: NavEvent) {
        when (navEvent) {
            is NavEvent.NavigateUp -> navigateUp()
            is NavEvent.NavigateToScreen -> navigateToScreen(navEvent.destination)
        }
    }


    @Composable
    override fun isRouteInGraphAsState(navigationRoute: NavigationRoute): State<Boolean> {
        return produceState(initialValue = false) {
            navController.currentBackStackEntryFlow.map { backStackEntry ->
                val currentDestination = backStackEntry.destination
                value = currentDestination.hierarchy.any { it.route == navigationRoute.name }
            }.collect()
        }
    }

    override fun navigateToScreen(destination: NavigationDestination) {
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

    override fun navigateUp() {
        navController.navigateUp()
    }

}