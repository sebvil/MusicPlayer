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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

fun interface EventHandler<E : UiEvent> {
    suspend fun onEvent(event: E)
}


@Composable
fun <E : UiEvent, S : State> HandleEvents(
    viewModel: BaseViewModel<E, S>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    eventHandler: EventHandler<E>
) {
    LaunchedEffect(key1 = viewModel.state) {
        viewModel.events.onEach { events ->
            val event = events.firstOrNull()
            if (event != null) {
                eventHandler.onEvent(event = event)
                viewModel.onEventHandled(event)
            }
        }.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED).collect()
    }
}


@Composable
fun <E : UiEvent, S : State> HandleNavEvents(
    viewModel: BaseViewModel<E, S>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    navigationDelegate: NavigationDelegate,
) {
    LaunchedEffect(key1 = viewModel.state) {
        viewModel.navEvents.onEach { events ->
            val event = events.firstOrNull()
            if (event != null) {
                navigationDelegate.handleNavEvent(event)
                viewModel.onNavEventHandled(event)
            }
        }.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED).collect()
    }
}