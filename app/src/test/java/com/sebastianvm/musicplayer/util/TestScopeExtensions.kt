package com.sebastianvm.musicplayer.util

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.util.Constants.DEFAULT_TIMEOUT
import io.kotest.core.test.testCoroutineScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
fun io.kotest.core.test.TestScope.advanceUntilIdle() {
    this.testCoroutineScheduler.advanceUntilIdle()
}

suspend fun <S : State> io.kotest.core.test.TestScope.testStateHolderState(
    stateHolder: StateHolder<S, *>,
    timeout: Duration? = null,
    name: String? = null,
    validate: suspend TurbineTestContext<S>.() -> Unit,
) {
    stateHolder.state.test(timeout, name) {
        advanceUntilIdle()
        validate()
    }
}
