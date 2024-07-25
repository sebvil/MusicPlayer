package com.sebastianvm.musicplayer.core.ui.navigation

interface NavController {
    fun push(uiComponent: UiComponent<*>, navOptions: NavOptions = NavOptions())

    fun pop()
}
