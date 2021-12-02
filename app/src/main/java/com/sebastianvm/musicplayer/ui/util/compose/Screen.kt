package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.EventHandler
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleEvents
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <A : UserAction, E : UiEvent, S : State> Screen(
    screenViewModel: BaseViewModel<A, E, S>,
    eventHandler: EventHandler<E>,
    bottomSheet: (@Composable (S, content: @Composable () -> Unit) -> Unit)? = null,
    topBar: @Composable (S) -> Unit = {},
    bottomNavBar: @Composable () -> Unit = {},
    fab: @Composable (S) -> Unit = {},
    content: @Composable (S) -> Unit
) {
    val state = screenViewModel.state.collectAsState(context = Dispatchers.Main)
    HandleEvents(eventsFlow = screenViewModel.eventsFlow, eventHandler = eventHandler)

    if (bottomSheet != null) {
        bottomSheet(state.value) {
            Scaffold(
                topBar = { topBar(state.value) },
                bottomBar = bottomNavBar,
                floatingActionButton = { fab(state.value) }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    content(state.value)
                }
            }
        }
    } else {
        Scaffold(
            topBar = { topBar(state.value) },
            bottomBar = bottomNavBar,
            floatingActionButton = { fab(state.value) }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                content(state.value)
            }
        }
    }

}