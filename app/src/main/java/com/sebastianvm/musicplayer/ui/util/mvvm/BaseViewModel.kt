package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.ui.util.mvvm.state.StateStore

abstract class BaseViewModel<A: UserAction, S: State>(initialState: S) : ViewModel() {

    private val stateStore = StateStore(initialState)
    val state = stateStore.stateLiveData


    fun setState(func: S.()-> S) {
       stateStore.setState(func)
    }


    fun <T: Any> observe(data: LiveData<T>, onChanged: (T) -> Unit) {
        stateStore.observe(data, onChanged)
    }

    abstract fun handle(action: A)
}