package com.sebastianvm.musicplayer.core.datastore.sort

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

@Single(binds = [SortPreferencesDataStore::class])
class DefaultSortPreferencesDataStore(
    private val context: Context,
    private val scope: CoroutineScope,
    @Named(DispatcherNames.IO) private val ioDispatcher: CoroutineDispatcher,
) :
    SortPreferencesDataStore,
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
