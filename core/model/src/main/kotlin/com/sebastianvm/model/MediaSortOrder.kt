package com.sebastianvm.model

enum class MediaSortOrder {
    ASCENDING,
    DESCENDING
}

operator fun MediaSortOrder.not(): MediaSortOrder {
    return when (this) {
        MediaSortOrder.ASCENDING -> MediaSortOrder.DESCENDING
        MediaSortOrder.DESCENDING -> MediaSortOrder.ASCENDING
    }
}
