package com.sebastianvm.musicplayer.core.commontest.extensions

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import io.kotest.core.test.TestScope
import io.kotest.core.test.testCoroutineScheduler
import kotlin.time.Duration
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
fun TestScope.advanceUntilIdle() {
    this.testCoroutineScheduler.advanceUntilIdle()
}

suspend fun <S : State> TestScope.testStateHolderState(
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
