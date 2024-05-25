package com.sebastianvm.musicplayer.features.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState

@Composable
fun AppNavigationHost(stateHolder: AppNavigationHostStateHolder, modifier: Modifier = Modifier) {
    val state by stateHolder.currentState
    AppNavigationHost(state, stateHolder::handle, modifier)
}

@Composable
fun AppNavigationHost(
    state: AppNavigationState,
    handle: Handler<AppNavigationAction>,
    modifier: Modifier = Modifier
) {
    val screens = state.backStack
    BackHandler(screens.size > 1) {
        handle(AppNavigationAction.PopBackStack)
    }
    screens.lastOrNull()?.Content(modifier = modifier)
}
