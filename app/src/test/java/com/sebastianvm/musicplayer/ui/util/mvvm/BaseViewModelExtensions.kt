package com.sebastianvm.musicplayer.ui.util.mvvm

import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first


suspend fun <E: UiEvent, S: State> BaseViewModel<E,S>.updateState() {
    state.drop(1).first()
}