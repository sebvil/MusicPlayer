package com.sebastianvm.musicplayer.ui.util.mvvm.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

fun interface EventHandler<E : UiEvent> {
    suspend fun onEvent(event: E)
}

@Composable
fun <E : UiEvent> HandleEvents(
    eventsFlow: Flow<E>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    eventHandler: EventHandler<E>
) {
    LaunchedEffect(key1 = eventsFlow) {
        eventsFlow.onEach { event ->
            eventHandler.onEvent(event = event)
        }.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.RESUMED).collect()
    }
}