package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.EventHandler
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleEvents
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <A: UserAction, E: UiEvent, S: State> Screen(
    screenViewModel: BaseViewModel<A,E,S>,
    eventHandler: EventHandler<E>,
    topBar: @Composable (S) -> Unit = {},
    fab: @Composable (S) -> Unit = {},
    content: @Composable (S) -> Unit
) {
    val state = screenViewModel.state.observeAsState(initial = screenViewModel.state.value)
    HandleEvents(eventsFlow = screenViewModel.eventsFlow, eventHandler = eventHandler)
    Scaffold(
        topBar = { topBar(state.value) },
        floatingActionButton = { fab(state.value) }
    ) {
        content(state.value)
    }

}