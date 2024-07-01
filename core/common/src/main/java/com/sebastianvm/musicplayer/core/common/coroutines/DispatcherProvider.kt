package com.sebastianvm.musicplayer.core.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val ioDispatcher: CoroutineDispatcher
}
