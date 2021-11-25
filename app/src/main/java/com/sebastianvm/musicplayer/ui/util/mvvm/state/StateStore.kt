package com.sebastianvm.musicplayer.ui.util.mvvm.state

import kotlinx.coroutines.flow.MutableStateFlow

class StateStore<S: State>(initialState: S) {

    val stateFlow = MutableStateFlow(initialState)


    fun setState(func: S.()-> S) {
        stateFlow.value = stateFlow.value.func()
    }


}