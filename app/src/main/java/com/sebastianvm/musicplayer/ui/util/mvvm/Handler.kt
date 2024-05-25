package com.sebastianvm.musicplayer.ui.util.mvvm

fun interface Handler<A : UserAction> {
    fun handle(action: A)

    operator fun invoke(action: A) = handle(action)
}
