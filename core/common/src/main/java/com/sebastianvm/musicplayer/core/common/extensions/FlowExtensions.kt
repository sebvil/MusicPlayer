package com.sebastianvm.musicplayer.core.common.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T, U> Flow<List<T>>.mapValues(transform: (T) -> U): Flow<List<U>> {
    return this.map { values -> values.map(transform) }
}
