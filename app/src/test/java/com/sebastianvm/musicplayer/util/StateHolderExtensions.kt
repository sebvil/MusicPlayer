package com.sebastianvm.musicplayer.util

import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState

fun <S> StateHolder<UiState<S>, *>.getDataState(): S {
    return (currentState as Data).state
}

val <S : State> StateHolder<S, *>.currentState: S
    get() = state.value

fun <S> UiState<S>.getDataState(): S {
    return (this as Data).state
}
