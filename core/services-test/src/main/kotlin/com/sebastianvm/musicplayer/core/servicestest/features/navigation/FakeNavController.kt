package com.sebastianvm.musicplayer.core.servicestest.features.navigation

import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.services.features.navigation.UiComponent

class FakeNavController : NavController {
    val backStack: MutableList<FakeBackstackEntry> = mutableListOf()

    override fun push(
        uiComponent: UiComponent<*, *>,
        navOptions: com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
    ) {
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
    val presentationMode:
        com.sebastianvm.musicplayer.core.ui.navigation.NavOptions.PresentationMode,
)
