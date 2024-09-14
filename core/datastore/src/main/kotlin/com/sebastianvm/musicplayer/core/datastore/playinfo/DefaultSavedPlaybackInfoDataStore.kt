package com.sebastianvm.musicplayer.core.datastore.playinfo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.sebastianvm.musicplayer.core.common.DispatcherNames
import com.sebastianvm.musicplayer.core.datastore.KotlinSerializationSerializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single(binds = [SavedPlaybackInfoDataStore::class])
class DefaultSavedPlaybackInfoDataStore(
    private val context: Context,
    private val scope: CoroutineScope,
    @Named(DispatcherNames.IO) private val ioDispatcher: CoroutineDispatcher,
) :
    SavedPlaybackInfoDataStore,
    DataStore<SavedPlaybackInfo> by DataStoreFactory.create(
        serializer =
            KotlinSerializationSerializer(
                defaultValue = SavedPlaybackInfo(),
                serializer = SavedPlaybackInfo.serializer(),
                ioDispatcher = ioDispatcher,
            ),
        scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
        migrations = emptyList(),
        produceFile = { context.dataStoreFile("now_playing_info.json") },
    )
