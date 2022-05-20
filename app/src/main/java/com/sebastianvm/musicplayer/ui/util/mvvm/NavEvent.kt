package com.sebastianvm.musicplayer.ui.util.mvvm

import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination

sealed class NavEvent {
    data class NavigateToScreen(val destination: NavigationDestination) : NavEvent()
    object NavigateUp : NavEvent()
}