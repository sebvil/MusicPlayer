package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class BaseViewModel<E : UiEvent, S : State>(initialState: S) :
    ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state

    private val _eventsFlow = MutableSharedFlow<E>(replay = 0)
    val eventsFlow: SharedFlow<E> = _eventsFlow

    fun setState(func: S.() -> S) {
        _state.value = _state.value.func()
    }

    fun addUiEvent(event: E) {
        viewModelScope.launch {
            _eventsFlow.emit(event)
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