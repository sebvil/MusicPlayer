package com.sebastianvm.musicplayer.core.datastore.sort

import kotlinx.serialization.Serializable

@Serializable
data class MediaSortPreferences<T : com.sebastianvm.musicplayer.core.model.SortOptions>(
    val sortOption: T,
    val sortOrder: com.sebastianvm.musicplayer.core.model.MediaSortOrder =
        com.sebastianvm.musicplayer.core.model.MediaSortOrder.ASCENDING,
)
