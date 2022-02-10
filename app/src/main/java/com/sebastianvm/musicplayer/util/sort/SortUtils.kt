package com.sebastianvm.musicplayer.util.sort

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.R
import java.text.Collator


fun mediaSortOptionFromResId(@StringRes resId: Int): MediaSortOption {
    return when (resId) {
        R.string.track_name -> MediaSortOption.TRACK
        R.string.artist_name -> MediaSortOption.ARTIST
        R.string.album_name -> MediaSortOption.ALBUM
        R.string.year -> MediaSortOption.YEAR
        R.string.track_number -> MediaSortOption.TRACK_NUMBER
        else -> throw IllegalStateException("Unknown sort option")
    }
}

val MediaSortOption.id: Int
    get() = when (this) {
        MediaSortOption.TRACK -> R.string.track_name
        MediaSortOption.ARTIST -> R.string.artist_name
        MediaSortOption.ALBUM -> R.string.album_name
        MediaSortOption.GENRE -> R.string.genres
        MediaSortOption.YEAR -> R.string.year
        MediaSortOption.TRACK_NUMBER -> R.string.track_number
        MediaSortOption.UNRECOGNIZED -> throw IllegalStateException("Unknown sort option")
    }


operator fun MediaSortOrder.not(): MediaSortOrder {
    return when (this) {
        MediaSortOrder.ASCENDING -> MediaSortOrder.DESCENDING
        MediaSortOrder.DESCENDING -> MediaSortOrder.ASCENDING
        else -> throw IllegalStateException("Unrecognized sort order")
    }
}

fun <T> getStringComparator(
    sortOrder: MediaSortOrder,
    sortBy: (T) -> String
): Comparator<T> {
    val collator = Collator.getInstance()
    collator.strength = Collator.PRIMARY
    return if (sortOrder == MediaSortOrder.ASCENDING) {
        Comparator.comparing(sortBy, collator)
    } else {
        Comparator.comparing(sortBy, collator.reversed())
    }
}

fun <T> getLongComparator(
    sortOrder: MediaSortOrder,
    sortBy: (T) -> Long
): Comparator<T> {
    return if (sortOrder == MediaSortOrder.ASCENDING) {
        compareBy { x: T -> sortBy(x) }
    } else {
        compareBy { x: T -> sortBy(x) }.reversed()
    }
}