package com.sebastianvm.musicplayer.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.sebastianvm.musicplayer.MusicPlayerApplication
import com.sebastianvm.musicplayer.core.common.coroutines.DefaultDispatcherProvider
import com.sebastianvm.musicplayer.core.common.coroutines.DispatcherProvider
import com.sebastianvm.musicplayer.core.data.di.DefaultRepositoryProvider
import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.database.di.DaoProvider
import com.sebastianvm.musicplayer.core.database.getDaoProvider
import com.sebastianvm.musicplayer.core.datastore.di.DataSourcesProvider
import com.sebastianvm.musicplayer.core.playback.manager.DefaultPlaybackManager
import com.sebastianvm.musicplayer.core.playback.manager.PlaybackManager
import com.sebastianvm.musicplayer.core.playback.player.MediaPlaybackClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppDependencies(private val appContext: Context) : Dependencies {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val database: DaoProvider by lazy {
        getDaoProvider(context = appContext, ioDispatcher = dispatcherProvider.ioDispatcher)
    }

    private val dataSourcesProvider: DataSourcesProvider by lazy { DataSourcesProvider(appContext) }

    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()

    override val repositoryProvider: RepositoryProvider by lazy {
        DefaultRepositoryProvider(
            context = appContext,
            dispatcherProvider = dispatcherProvider,
            database = database,
            dataSourcesProvider = dataSourcesProvider,
        )
    }

    override val playbackManager: PlaybackManager by lazy {
        DefaultPlaybackManager(
            mediaPlaybackClient = mediaPlaybackClient,
            trackRepository = repositoryProvider.trackRepository,
        )
    }

    private val mediaPlaybackClient: MediaPlaybackClient by lazy {
        MediaPlaybackClient(context = appContext, externalScope = applicationScope)
    }
}

@Composable
fun dependencies(): Dependencies =
    (LocalContext.current.applicationContext as MusicPlayerApplication).dependencies
