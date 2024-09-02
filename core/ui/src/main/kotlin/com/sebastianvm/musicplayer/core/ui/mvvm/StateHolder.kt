package com.sebastianvm.musicplayer.core.ui.mvvm

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow

interface StateHolder<S : State, A : UserAction> {
    val state: StateFlow<S>

    fun handle(action: A)
}

val <S : State, A : UserAction> StateHolder<S, A>.currentState: androidx.compose.runtime.State<S>
    @Composable get() = state.collectAsStateWithLifecycle()
