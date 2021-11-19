package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.ui.util.mvvm.state.StateStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<A: UserAction, E: UiEvent, S: State>(initialState: S) : ViewModel() {

    private val stateStore = StateStore(initialState)
    val state = stateStore.stateLiveData

    private val _events = Channel<E>(0)
    val eventsFlow = _events.receiveAsFlow() // read-on

    fun setState(func: S.()-> S) {
       stateStore.setState(func)
    }

    fun addUiEvent(event: E) {
        viewModelScope.launch {
           val e =  _events.trySend(event)
        }
    }

    fun <T: Any> observe(data: LiveData<T>, onChanged: (T) -> Unit) {
        stateStore.observe(data, onChanged)
    }

    abstract fun handle(action: A)
}