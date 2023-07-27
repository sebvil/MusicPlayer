package com.sebastianvm.musicplayer.ui.util.mvvm.events

import com.ramcosta.composedestinations.spec.Direction

sealed class NavEvent {
    data class NavigateToScreen(val destination: Direction) : NavEvent()
    object NavigateUp : NavEvent()
}