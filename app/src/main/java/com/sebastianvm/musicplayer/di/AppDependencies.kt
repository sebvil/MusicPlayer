package com.sebastianvm.musicplayer.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.sebastianvm.database.MusicDatabase
import com.sebastianvm.musicplayer.MusicPlayerApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppDependencies(private val appContext: Context) : Dependencies {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val database: MusicDatabase by lazy {
        Room.databaseBuilder(appContext, MusicDatabase::class.java, "music_database")
            .setQueryCoroutineContext(context = dispatcherProvider.ioDispatcher)
            .build()
    }

    private val jetpackDataStoreProvider: JetpackDataStoreProvider by lazy {
        JetpackDataStoreProvider(appContext)
    }

    private val dispatcherProvider: DispatcherProvider = DispatcherProvider

    override val repositoryProvider: RepositoryProvider by lazy {
        AppRepositoryProvider(
            context = appContext,
            dispatcherProvider = dispatcherProvider,
            database = database,
            jetpackDataStoreProvider = jetpackDataStoreProvider,
            applicationScope = applicationScope,
        )
    }
}

@Composable
fun dependencies(): Dependencies =
    (LocalContext.current.applicationContext as MusicPlayerApplication).dependencies
