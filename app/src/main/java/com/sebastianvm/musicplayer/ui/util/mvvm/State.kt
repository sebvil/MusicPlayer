package com.sebastianvm.musicplayer.ui.util.mvvm

import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent

interface State<E: UiEvent> {
    val events: List<E>

    fun  <S: State<E>> setEvent(events: List<E>) : S
}