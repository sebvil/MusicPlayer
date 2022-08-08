package com.sebastianvm.musicplayer.ui.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.NavEvent

class NavigationDelegate(private val navController: NavController) {

    fun handleNavEvent(navEvent: NavEvent) {
        when (navEvent) {
            is NavEvent.NavigateUp -> navigateUp()
            is NavEvent.NavigateToScreen -> navigateToScreen(navEvent.destination)
        }
    }

    fun isRouteInGraph(navigationRoute: NavigationRoute): Boolean {
        val navBackStackEntry = navController.currentBackStackEntry
        val currentDestination = navBackStackEntry?.destination
        Log.i("Nav", "${currentDestination?.hierarchy?.map { it.route }}")
        Log.i("Nav", "${navigationRoute.name}")
        return currentDestination?.hierarchy?.any { it.route == navigationRoute.name } == true
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