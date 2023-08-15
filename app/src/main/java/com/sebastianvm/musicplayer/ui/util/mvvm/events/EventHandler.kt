package com.sebastianvm.musicplayer.ui.util.mvvm.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun <S : State, A : UserAction> HandleNavEvents(
    viewModel: BaseViewModel<S, A>,
    navigationDelegate: NavigationDelegate,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    LaunchedEffect(key1 = viewModel.navEvents) {
        viewModel.navEvents.onEach { events ->
            val event = events.firstOrNull()
            if (event != null) {
                navigationDelegate.handleNavEvent(event)
                viewModel.onNavEventHandled(event)
            }
        }.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED).collect()
    }
}
