package com.sebastianvm.musicplayer.util

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

fun <T> getStringComparator(sortOrder: SortOrder, sortBy: (T) -> String): Comparator<T>{
    val collator = Collator.getInstance()
    collator.strength = Collator.PRIMARY
    return if (sortOrder == SortOrder.ASCENDING) {
        Comparator.comparing(sortBy, collator)
    } else {
        Comparator.comparing(sortBy, collator.reversed())
    }
}