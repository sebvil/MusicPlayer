package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.ui.util.mvvm.state.StateStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<A : UserAction, E : UiEvent, S : State>(initialState: S) :
    ViewModel() {

    private val stateStore = StateStore(initialState)
    val state = stateStore.stateLiveData

    private val _nonBlockingEvents = Channel<E>(Channel.BUFFERED)
    private val _blockingEvents = Channel<E>(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val eventsFlow = merge(_nonBlockingEvents.receiveAsFlow(), _blockingEvents.receiveAsFlow())

    // This is an event that will block any other events from being sent until said event has been collected.
    // This is useful for nav events, for example. We do not want to accept any more nav events after we
    // have triggered one.

    fun setState(func: S.() -> S) {
        stateStore.setState(func)
    }

    fun addUiEvent(event: E) {
        viewModelScope.launch {
            _nonBlockingEvents.trySend(event)
        }
    }

    fun addBlockingEvent(event: E) {
        viewModelScope.launch {
            _blockingEvents.trySend(event)
        }
    }

    fun <T : Any> observe(data: LiveData<T>, onChanged: (T) -> Unit) {
        stateStore.observe(data, onChanged)
    }

    abstract fun handle(action: A)
}