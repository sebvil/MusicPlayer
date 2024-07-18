package com.sebastianvm.musicplayer.services.features.mvvm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

class CloseableCoroutineScope(context: CoroutineContext) : java.io.Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancel()
    }
}

fun stateHolderScope(): CloseableCoroutineScope =
    CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
