package com.sebastianvm.musicplayer.features.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.designsystem.components.BottomSheet
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState

@Composable
fun AppNavigationHost(stateHolder: AppNavigationHostStateHolder, modifier: Modifier = Modifier) {
    val state by stateHolder.currentState
    AppNavigationHost(state, stateHolder::handle, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
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
    screens.lastOrNull { it.presentationMode == NavOptions.PresentationMode.Screen }?.screen?.Content(
        modifier = modifier
    )

    screens.lastOrNull { it.presentationMode == NavOptions.PresentationMode.BottomSheet }?.screen?.let {
        BottomSheet(onDismissRequest = {
            handle(AppNavigationAction.PopBackStack)
        }) {
            it.Content(
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}
