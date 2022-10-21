package com.sebastianvm.musicplayer.ui.util.mvvm

object DefaultScreenDelegateProvider {

    fun <A : UserAction> getDefaultInstance(): ScreenDelegate<A> =
        object : ScreenDelegate<A> {
            override fun handle(action: A) = Unit
        }
}

interface ScreenDelegate<A : UserAction> {
    fun handle(action: A)
}

