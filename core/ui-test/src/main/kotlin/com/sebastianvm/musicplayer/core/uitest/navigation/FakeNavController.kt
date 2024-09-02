package com.sebastianvm.musicplayer.core.uitest.navigation

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions

class FakeNavController : NavController {
    val backStack = mutableListOf<FakeBackstackEntry>()

    override fun push(mvvmComponent: MvvmComponent, navOptions: NavOptions) {
        backStack.add(FakeBackstackEntry(mvvmComponent, navOptions))
    }

    override fun pop() {
        backStack.removeAt(backStack.lastIndex)
    }
}

data class FakeBackstackEntry(val mvvmComponent: MvvmComponent, val navOptions: NavOptions)
