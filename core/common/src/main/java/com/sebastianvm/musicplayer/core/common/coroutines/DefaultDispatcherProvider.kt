@file:Suppress("InjectDispatcher")

package com.sebastianvm.musicplayer.core.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultDispatcherProvider : DispatcherProvider {
    override val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
}
