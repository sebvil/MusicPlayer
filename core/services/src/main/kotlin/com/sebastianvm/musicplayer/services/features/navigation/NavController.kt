package com.sebastianvm.musicplayer.services.features.navigation

interface NavController {
    fun push(uiComponent: UiComponent<*, *>, navOptions: NavOptions = NavOptions())

    fun pop()
}
