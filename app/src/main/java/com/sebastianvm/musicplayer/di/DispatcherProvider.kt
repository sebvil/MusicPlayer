package com.sebastianvm.musicplayer.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object DispatcherProvider {
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
}
