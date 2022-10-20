package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.EventHandler
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleEvents
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleNavEvents
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <S : State, A : UserAction, E : UiEvent> Screen(
    screenViewModel: BaseViewModel<S, A, E>,
    eventHandler: EventHandler<E>,
    navigationDelegate: NavigationDelegate,
    modifier: Modifier = Modifier,
    topBar: @Composable (S) -> Unit = {},
    fab: @Composable (S) -> Unit = {},
    content: @Composable (S) -> Unit
) {
    val state = screenViewModel.stateFlow.collectAsState(context = Dispatchers.Main)
    HandleEvents(viewModel = screenViewModel, eventHandler = eventHandler)
    HandleNavEvents(viewModel = screenViewModel, navigationDelegate = navigationDelegate)
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                focusManager.clearFocus()
            },
        topBar = { topBar(state.value) },
        floatingActionButton = { fab(state.value) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            content(state.value)
        }
    }
}


@Composable
fun <S : State, A : UserAction, E : UiEvent> NewScreen(
    screenViewModel: BaseViewModel<S, A, E>,
    eventHandler: EventHandler<E>,
    navigationDelegate: NavigationDelegate,
    screen: @Composable (S, ScreenDelegate<A>) -> Unit
) {
    val state = screenViewModel.stateFlow.collectAsState(context = Dispatchers.Main)
    HandleEvents(viewModel = screenViewModel, eventHandler = eventHandler)
    HandleNavEvents(viewModel = screenViewModel, navigationDelegate = navigationDelegate)
    screen(state.value, screenViewModel)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenLayout(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                focusManager.clearFocus()
            },
        topBar = { topBar() },
        floatingActionButton = { fab() },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            content()
        }
    }
}

