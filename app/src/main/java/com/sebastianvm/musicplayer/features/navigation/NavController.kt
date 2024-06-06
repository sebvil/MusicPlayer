package com.sebastianvm.musicplayer.features.navigation

interface NavController {
    fun push(uiComponent: UiComponent<*, *>, navOptions: NavOptions = NavOptions())

    fun pop()
}
