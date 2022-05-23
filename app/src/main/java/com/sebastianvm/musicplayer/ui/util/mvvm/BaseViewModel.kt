package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<E : UiEvent, S : State>(initialState: S) :
    ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state


    private val _events: MutableStateFlow<List<E>> = MutableStateFlow(listOf())
    val events: StateFlow<List<E>> = _events

    private val _navEvents: MutableStateFlow<List<NavEvent>> =
        MutableStateFlow(listOf())
    val navEvents: StateFlow<List<NavEvent>> = _navEvents

    fun setState(func: S.() -> S) {
        _state.update {
            it.func()
        }
    }

    fun addUiEvent(event: E) {
        _events.update {
            val newEvents = it.toMutableList()
            newEvents.add(event)
            newEvents
        }
    }

    fun onEventHandled(event: E) {
        _events.update {
            val newEvents = it.toMutableList()
            newEvents.remove(event)
            newEvents
        }
    }

    fun addNavEvent(navEvent: NavEvent) {
        _navEvents.update {
            val newEvents = it.toMutableList()
            newEvents.add(navEvent)
            newEvents
        }
    }


    fun onNavEventHandled(navEvent: NavEvent) {
        _navEvents.update {
            val newEvents = it.toMutableList()
            newEvents.remove(navEvent)
            newEvents
        }
    }

    fun <T> collect(flow: Flow<T>, onChanged: suspend (T) -> Unit) {
        viewModelScope.launch {
            flow.collect {
                onChanged(it)
            }
        }
    }
}