package com.sebastianvm.musicplayer.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

class NavigationDelegate(private val navController: NavController) {

    fun navigateToScreen(destination: NavigationDestination) {
        val navBackStackEntry = navController.currentBackStackEntry
        val currentDestination = navBackStackEntry?.destination

        if (currentDestination?.hierarchy?.any { it.route == destination.navigationRoute.name } == true) {
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

    fun navigateUp() {
        navController.navigateUp()
    }

}