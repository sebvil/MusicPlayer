package com.sebastianvm.musicplayer.util.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine


fun <T1, T2> combineToPair(f1: Flow<T1>, f2: Flow<T2>): Flow<Pair<T1, T2>> {
    return combine(f1, f2) { v1, v2 -> Pair(v1, v2) }
}