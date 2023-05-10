package com.sebastianvm.musicplayer.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination


fun NavGraphBuilder.mainNavDestination(
    navigationDelegate: NavigationDelegate,
    paddingValues: PaddingValues,
    content: @Composable (page: TopLevelScreen, paddingValues: PaddingValues) -> Unit
) {
    screenDestination<MainViewModel>(
        destination = NavigationRoute.MainRoot,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        MainScreen(paddingValues, content)
    }
}