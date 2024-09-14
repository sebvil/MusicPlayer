package com.sebastianvm.musicplayer.core.datastore.playinfo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.sebastianvm.musicplayer.annotations.AppScope
import com.sebastianvm.musicplayer.annotations.IoDispatcher
import com.sebastianvm.musicplayer.core.datastore.KotlinSerializationSerializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class SavedPlaybackInfoDataStore(
    private val context: Context,
    private val scope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) :
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
