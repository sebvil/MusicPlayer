package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.lifecycle.ViewModel
import com.google.common.annotations.VisibleForTesting
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S : State, A : UserAction> : ViewModel() {

    private val _state: MutableStateFlow<UiState<S>> = MutableStateFlow(Loading)
    val stateFlow: StateFlow<UiState<S>> = _state

    @VisibleForTesting
    val state get() = _state.value

    val dataState: S?
        get() = (_state.value as? Data)?.state

    private val _navEvents: MutableStateFlow<List<NavEvent>> =
        MutableStateFlow(listOf())
    val navEvents: StateFlow<List<NavEvent>> = _navEvents

    fun onNavEventHandled(navEvent: NavEvent) {
        _navEvents.update {
            val newEvents = it.toMutableList()
            newEvents.remove(navEvent)
            newEvents
        }
    }

    protected fun setState(func: (UiState<S>) -> UiState<S>) {
        _state.update {
            func(it)
        }
    }

    protected fun setDataState(func: (S) -> S) {
        setState { Data(func((it as? Data)?.state ?: defaultState)) }
    }

    protected fun addNavEvent(navEvent: NavEvent) {
        _navEvents.update {
            val newEvents = it.toMutableList()
            newEvents.add(navEvent)
            newEvents
        }
    }

    abstract fun handle(action: A)

    abstract val defaultState: S
}
