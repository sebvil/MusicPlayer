package com.sebastianvm.musicplayer.ui.util.mvvm.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

fun interface EventHandler<E : UiEvent> {
    suspend fun onEvent(event: E)
}

@Composable
fun <E : UiEvent, S: State<E>> HandleEvents(
    viewModel: BaseViewModel<E,S>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    eventHandler: EventHandler<E>
) {
    LaunchedEffect(key1 = viewModel.state) {
        viewModel.state.map { it.events }.onEach { event ->
            if (event != null) {
                eventHandler.onEvent(event = event)
                viewModel.onEventHandled()
            }
        }.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED).collect()
    }
}