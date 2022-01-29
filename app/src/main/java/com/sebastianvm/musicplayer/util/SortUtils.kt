package com.sebastianvm.musicplayer.util

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.R
import java.text.Collator

enum class SortOrder {
    ASCENDING,
    DESCENDING;

    operator fun not(): SortOrder {
        return if (this == ASCENDING) {
            DESCENDING
        } else {
            ASCENDING
        }
    }
}

enum class SortOption(@StringRes val id: Int) {
    TRACK_NAME(R.string.track_name),
    ARTIST_NAME(R.string.artist_name),
    ALBUM_NAME(R.string.album_name),
    YEAR(R.string.year),
    TRACK_NUMBER(R.string.track_number);

    companion object {
        fun fromResId(@StringRes resId: Int): SortOption {
            return when (resId) {
                R.string.track_name -> TRACK_NAME
                R.string.artist_name -> ARTIST_NAME
                R.string.album_name -> ALBUM_NAME
                R.string.year -> YEAR
                R.string.track_number -> TRACK_NUMBER
                else -> throw IllegalStateException("Unknown sort option")
            }
        }
    }
}

data class SortSettings(
    val sortOption: SortOption,
    val sortOrder: SortOrder
)

fun <T> getStringComparator(sortOrder: SortOrder, sortBy: (T) -> String): Comparator<T>{
    val collator = Collator.getInstance()
    collator.strength = Collator.PRIMARY
    return if (sortOrder == SortOrder.ASCENDING) {
        Comparator.comparing(sortBy, collator)
    } else {
        Comparator.comparing(sortBy, collator.reversed())
    }
}