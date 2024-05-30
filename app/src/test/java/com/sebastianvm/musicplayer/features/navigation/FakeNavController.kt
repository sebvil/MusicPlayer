package com.sebastianvm.musicplayer.features.navigation

class FakeNavController : NavController {
    val backStack: MutableList<BackStackEntry> = mutableListOf()
    override fun push(screen: Screen<*, *>, navOptions: NavOptions) {
        val entry = BackStackEntry(screen, navOptions.presentationMode)
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
