package com.sebastianvm.musicplayer.core.datastore.sort

import kotlinx.coroutines.flow.Flow

interface SortPreferencesDataSource {
    val data: Flow<SortPreferences>

    suspend fun updateData(update: (SortPreferences) -> SortPreferences)
}
