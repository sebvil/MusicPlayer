package com.sebastianvm.musicplayer.util

import app.cash.turbine.ReceiveTurbine

@Suppress("UNCHECKED_CAST")
suspend fun <T> ReceiveTurbine<*>.awaitItemAs(): T {
    return awaitItem() as T
}
