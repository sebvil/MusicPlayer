package com.sebastianvm.musicplayer.ui.util.mvvm

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ViewModelInterface<S : State, A : UserAction> {
    val state: StateFlow<S>
    fun handle(action: A)
}

object DefaultViewModelInterfaceProvider {

    fun <S : State, A : UserAction> getDefaultInstance(defaultState: S): ViewModelInterface<S, A> =
        object : ViewModelInterface<S, A> {
            override val state: StateFlow<S>
                get() = MutableStateFlow(defaultState)

            override fun handle(action: A) = Unit
        }
}