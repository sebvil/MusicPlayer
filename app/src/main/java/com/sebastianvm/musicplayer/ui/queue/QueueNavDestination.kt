package com.sebastianvm.musicplayer.ui.queue

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute

fun NavGraphBuilder.queueNavDestination() {
    composable(
        route = createNavRoute(NavRoutes.QUEUE)
    ) {
        val screenViewModel: QueueViewModel = hiltViewModel()
        QueueScreen(screenViewModel = screenViewModel)
    }
}