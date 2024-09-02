package com.sebastianvm.musicplayer.core.ui.navigation

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent

interface NavController {
    fun push(mvvmComponent: MvvmComponent, navOptions: NavOptions = NavOptions())

    fun pop()
}
