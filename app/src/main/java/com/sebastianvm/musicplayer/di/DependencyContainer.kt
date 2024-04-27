package com.sebastianvm.musicplayer.di

import android.content.Context
import androidx.room.Room
import com.sebastianvm.musicplayer.database.MusicDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class DependencyContainer(private val appContext: Context) {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val database: MusicDatabase by lazy {
        Room.databaseBuilder(
            appContext,
            MusicDatabase::class.java,
            "music_database"
        ).build()
    }

    val dispatcherProvider: DispatcherProvider = DispatcherProvider

    private val jetpackDataStoreProvider: JetpackDataStoreProvider by lazy {
        JetpackDataStoreProvider(appContext)
    }

    val repositoryProvider: RepositoryProvider
        get() = RepositoryProvider(
            context = appContext,
            dispatcherProvider = dispatcherProvider,
            database = database,
            jetpackDataStoreProvider = jetpackDataStoreProvider,
            applicationScope = applicationScope
        )
}