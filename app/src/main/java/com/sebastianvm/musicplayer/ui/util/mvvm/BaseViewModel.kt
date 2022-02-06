package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.ui.util.mvvm.state.StateStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

abstract class BaseViewModel<A : UserAction, E : UiEvent, S : State>(initialState: S) :
    ViewModel() {

    private val stateStore = StateStore(initialState)
    val state = stateStore.stateFlow.asStateFlow()

    private val _eventsFlow = MutableSharedFlow<E>(replay = 0)
    val eventsFlow: SharedFlow<E> = _eventsFlow

    fun setState(func: S.() -> S) {
        stateStore.setState(func)
    }

    fun addUiEvent(event: E) {
        viewModelScope.launch {
            _eventsFlow.emit(event)
        }
    }


    fun <T> collect(flow: Flow<T>, onChanged: suspend (T) -> Unit) {
        launchViewModelIOScope {
            flow.collect {
                onChanged(it)
            }
        }
    }


    fun <T> collectFirst(flow: Flow<T>, onChanged: suspend (T) -> Unit) {
        launchViewModelIOScope {
            onChanged(flow.first())
        }

    }


    abstract fun handle(action: A)
}