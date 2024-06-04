package com.sebastianvm.musicplayer.util.extensions

inline fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
    return indexOfFirst(predicate).takeUnless { it == -1 }
}
