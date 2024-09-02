package com.sebastianvm.musicplayer.core.ui.mvvm

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<S : State, A : UserAction>(
    viewModelScope: CoroutineScope = getViewModelScope()
) : ViewModel(viewModelScope = viewModelScope) {
    abstract val state: StateFlow<S>

    abstract fun handle(action: A)
}

fun getViewModelScope(): CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

val <S : State, A : UserAction> BaseViewModel<S, A>.currentState: androidx.compose.runtime.State<S>
    @Composable get() = state.collectAsStateWithLifecycle()
