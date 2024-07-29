package com.sebastianvm.musicplayer.core.datastore.sort

import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.model.SortOptions
import kotlinx.serialization.Serializable

@Serializable
data class MediaSortPreferences<T : SortOptions>(
    val sortOption: T,
    val sortOrder: MediaSortOrder = MediaSortOrder.ASCENDING,
)
