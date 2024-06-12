package com.sebastianvm.musicplayer.ui.util

import androidx.compose.ui.platform.AndroidUiDispatcher
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class CloseableCoroutineScope(context: CoroutineContext) : java.io.Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancel()
    }
}

fun stateHolderScope(): CloseableCoroutineScope =
    CloseableCoroutineScope(SupervisorJob() + AndroidUiDispatcher.Main)
