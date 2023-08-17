package com.sebastianvm.musicplayer.util

import com.sebastianvm.musicplayer.util.Constants.DEFAULT_TIMEOUT
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private object Constants {
    val DEFAULT_TIMEOUT = 10.seconds
}

fun TestScope.runSafeTest(
    timeout: Duration = DEFAULT_TIMEOUT,
    testBody: suspend TestScope.() -> Unit
) = this.runTest(timeout) {
    testBody()
    this.coroutineContext.cancelChildren()
}
