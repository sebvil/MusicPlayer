package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.EventHandler
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleEvents
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <E : UiEvent, S : State<E>> Screen(
    screenViewModel: BaseViewModel<E, S>,
    eventHandler: EventHandler<E>,
    modifier: Modifier = Modifier,
    topBar: @Composable (S) -> Unit = {},
    fab: @Composable (S) -> Unit = {},
    content: @Composable (S) -> Unit
) {
    val state = screenViewModel.state.collectAsState(context = Dispatchers.Main)
    HandleEvents(viewModel = screenViewModel, eventHandler = eventHandler)

    Scaffold(
        modifier = modifier,
        topBar = { topBar(state.value) },
        floatingActionButton = { fab(state.value) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            content(state.value)
        }
    }
}
