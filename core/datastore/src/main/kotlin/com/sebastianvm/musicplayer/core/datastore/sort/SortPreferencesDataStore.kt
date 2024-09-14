package com.sebastianvm.musicplayer.core.datastore.sort

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
class SortPreferencesDataStore(
    private val context: Context,
    private val scope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) :
    DataStore<SortPreferences> by DataStoreFactory.create(
        serializer =
            KotlinSerializationSerializer(
                defaultValue = SortPreferences(),
                serializer = SortPreferences.serializer(),
                ioDispatcher = ioDispatcher,
            ),
        scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
        migrations = emptyList(),
        produceFile = { context.dataStoreFile("sort_prefs.json") },
    )
