package com.sebastianvm.musicplayer.core.uitest.navigation

import com.sebastianvm.musicplayer.core.ui.mvvm.UiComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions

class FakeNavController : NavController {
    val backStack = mutableListOf<FakeBackstackEntry>()

    override fun push(mvvmComponent: UiComponent, navOptions: NavOptions) {
        backStack.add(FakeBackstackEntry(mvvmComponent, navOptions))
    }

    override fun pop() {
        backStack.removeAt(backStack.lastIndex)
    }
}

data class FakeBackstackEntry(val mvvmComponent: UiComponent, val navOptions: NavOptions)
