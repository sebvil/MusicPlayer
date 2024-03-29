package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S : State, A : UserAction>(
    initialState: S,
    viewModelScope: CoroutineScope? = null
) :
    ViewModel() {

    protected val vmScope: CoroutineScope = viewModelScope ?: this.viewModelScope

    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState)
    val stateFlow: StateFlow<S> = _state

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val state get() = _state.value

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

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun setState(func: (S) -> S) {
        _state.update(func)
    }

    protected fun addNavEvent(navEvent: NavEvent) {
        _navEvents.update {
            val newEvents = it.toMutableList()
            newEvents.add(navEvent)
            newEvents
        }
    }

    abstract fun handle(action: A)
}
