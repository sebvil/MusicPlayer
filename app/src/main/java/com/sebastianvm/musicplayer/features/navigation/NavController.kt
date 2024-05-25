package com.sebastianvm.musicplayer.features.navigation

import com.sebastianvm.musicplayer.ui.util.mvvm.Delegate

interface NavController : Delegate {
    fun push(screen: Screen<*>)
    fun pop()
}
