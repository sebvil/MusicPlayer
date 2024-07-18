package com.sebastianvm.musicplayer.core.ui.mvvm

fun interface Handler<A : UserAction> {
    fun handle(action: A)

    operator fun invoke(action: A) = handle(action)
}
