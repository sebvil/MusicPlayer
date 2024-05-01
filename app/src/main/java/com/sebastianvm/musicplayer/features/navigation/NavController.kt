package com.sebastianvm.musicplayer.features.navigation

interface NavController {
    fun push(screen: Screen<*>)
    fun pop()
}