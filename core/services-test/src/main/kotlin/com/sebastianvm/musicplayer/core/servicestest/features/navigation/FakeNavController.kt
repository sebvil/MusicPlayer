package com.sebastianvm.musicplayer.core.servicestest.features.navigation

import com.sebastianvm.musicplayer.services.features.mvvm.Arguments
import com.sebastianvm.musicplayer.services.features.navigation.NavController
import com.sebastianvm.musicplayer.services.features.navigation.NavOptions
import com.sebastianvm.musicplayer.services.features.navigation.UiComponent

class FakeNavController : NavController {
    val backStack: MutableList<FakeBackstackEntry> = mutableListOf()

    override fun push(uiComponent: UiComponent<*, *>, navOptions: NavOptions) {
        val entry = FakeBackstackEntry(uiComponent.arguments, navOptions.presentationMode)
        if (navOptions.popCurrent) {
            backStack.removeLast()
            backStack.add(entry)
        } else {
            backStack.add(entry)
        }
    }

    override fun pop() {
        backStack.removeLast()
    }
}

data class FakeBackstackEntry(
    val arguments: Arguments,
    val presentationMode: NavOptions.PresentationMode,
)
