package com.sebastianvm.musicplayer.core.ui.mvvm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class BaseViewModel<S : State, A : UserAction>(viewModelScope: CoroutineScope = getViewModelScope()) :
    StateHolder<S, A>, ViewModel(viewModelScope = viewModelScope)

fun getViewModelScope(): CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)