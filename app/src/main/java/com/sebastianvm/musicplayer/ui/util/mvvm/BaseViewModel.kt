package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.lifecycle.ViewModel
import com.google.common.annotations.VisibleForTesting
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S : State, A : UserAction>(initialState: S) :
    ScreenDelegate<A>, ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val stateFlow: StateFlow<S> = _state

    @VisibleForTesting
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

    protected fun setState(func: S.() -> S) {
        _state.update {
            it.func()
        }
    }

    protected fun addNavEvent(navEvent: NavEvent) {
        _navEvents.update {
            val newEvents = it.toMutableList()
            newEvents.add(navEvent)
            newEvents
        }
    }

}