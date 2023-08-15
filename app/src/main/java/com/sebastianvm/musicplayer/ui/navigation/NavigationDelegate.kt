package com.sebastianvm.musicplayer.ui.navigation

import androidx.navigation.NavOptionsBuilder
import com.ramcosta.composedestinations.spec.Direction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent

interface NavigationDelegate {

    fun handleNavEvent(navEvent: NavEvent)

    fun navigateUp()

    fun navigateToScreen(
        destination: Direction,
        onlyIfResumed: Boolean = true,
        builder: NavOptionsBuilder.() -> Unit = {}
    )
}
