package com.sebastianvm.musicplayer.util.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

fun <T, U> Flow<List<T>>.mapValues(transform: (T) -> U): Flow<List<U>> {
    return this.map { values -> values.map(transform) }
}

@Composable
fun <T> Flow<T>.collectValue(initial: T): T {
    return this.collectAsState(initial = initial).value
}

@Composable
fun <T> StateFlow<T>.collectValue(): T {
    return this.collectAsState().value
}
