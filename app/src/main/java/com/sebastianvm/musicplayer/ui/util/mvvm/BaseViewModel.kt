package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<E : UiEvent, S : State<E>>(initialState: S) :
    ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state

    fun setState(func: S.() -> S) {
        _state.update {
            it.func()
        }
    }

    fun addUiEvent(event: E) {
        val events = _state.value.events.toMutableList()
        events.add(event)
        setState { setEvent(events = events) }
    }

    fun onEventHandled(event: E)  {
        val events = _state.value.events.toMutableList()
        events.remove(event)
        setState { setEvent(events = events) }
    }

    fun <T> collect(flow: Flow<T>, onChanged: suspend (T) -> Unit) {
        viewModelScope.launch {
            flow.collect {
                onChanged(it)
            }
        }
    }
}