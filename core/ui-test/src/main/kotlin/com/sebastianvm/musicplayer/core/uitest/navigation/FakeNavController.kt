package com.sebastianvm.musicplayer.core.uitest.navigation

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent

class FakeNavController : NavController {
    val backStack = mutableListOf<FakeBackstackEntry>()

    override fun push(uiComponent: UiComponent<*>, navOptions: NavOptions) {
        backStack.add(FakeBackstackEntry(uiComponent, navOptions))
    }

    override fun pop() {
        backStack.removeAt(backStack.lastIndex)
    }
}

data class FakeBackstackEntry(val uiComponent: UiComponent<*>, val navOptions: NavOptions)
