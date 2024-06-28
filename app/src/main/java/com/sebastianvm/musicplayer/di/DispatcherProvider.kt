@file:Suppress("InjectDispatcher")

package com.sebastianvm.musicplayer.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object DispatcherProvider {
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
}
