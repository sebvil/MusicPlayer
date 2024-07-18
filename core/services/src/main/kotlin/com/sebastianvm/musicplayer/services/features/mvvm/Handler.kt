package com.sebastianvm.musicplayer.services.features.mvvm

fun interface Handler<A : UserAction> {
    fun handle(action: A)

    operator fun invoke(action: A) = handle(action)
}
