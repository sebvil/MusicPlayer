package com.sebastianvm.musicplayer.ui.navigation

import androidx.navigation.NavController

class NavigationDelegate(private val navController: NavController) {

    fun navigateToScreen(destination: NavigationDestination) {
        navController.navigateTo(destination)
    }

    fun navigateUp() {
        navController.navigateUp()
    }

}