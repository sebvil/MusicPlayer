package com.sebastianvm.musicplayer.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState

@Composable
fun AppNavigationHost(stateHolder: AppNavigationHostStateHolder, modifier: Modifier = Modifier) {
    val state by stateHolder.currentState
    AppNavigationHost(state, modifier)
}

@Composable
fun AppNavigationHost(state: AppNavigationState, modifier: Modifier = Modifier) {
    state.backStack.last().Content(modifier = modifier)
}