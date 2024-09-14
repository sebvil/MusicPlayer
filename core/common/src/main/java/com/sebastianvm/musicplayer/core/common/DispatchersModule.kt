@file:Suppress("InjectDispatcher")

package com.sebastianvm.musicplayer.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single @Named(DispatcherNames.IO) fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

@Single
@Named(DispatcherNames.MAIN)
fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

@Single
@Named(DispatcherNames.DEFAULT)
fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

@Single
fun applicationScope(): CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
