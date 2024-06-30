package com.sebastianvm.musicplayer.core.datastore.sort

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow

internal class DefaultSortPreferencesDataSource(private val dataStore: DataStore<SortPreferences>) :
    SortPreferencesDataSource {
    override val data: Flow<SortPreferences> = dataStore.data

    override suspend fun updateData(update: (SortPreferences) -> SortPreferences) {
        dataStore.updateData(update)
    }
}
