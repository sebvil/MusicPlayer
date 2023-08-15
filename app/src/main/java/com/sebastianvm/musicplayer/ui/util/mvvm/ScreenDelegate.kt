package com.sebastianvm.musicplayer.ui.util.mvvm

fun interface ScreenDelegate<A : UserAction> {
    fun handle(action: A)
}
