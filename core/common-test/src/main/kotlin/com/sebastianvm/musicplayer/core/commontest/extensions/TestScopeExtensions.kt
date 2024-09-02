package com.sebastianvm.musicplayer.core.commontest.extensions

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import io.kotest.core.test.TestScope
import io.kotest.core.test.testCoroutineScheduler
import kotlin.time.Duration
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
fun TestScope.advanceUntilIdle() {
    this.testCoroutineScheduler.advanceUntilIdle()
}

suspend fun <S : State> TestScope.testViewModelState(
    viewModel: BaseViewModel<S, *>,
    timeout: Duration? = null,
    name: String? = null,
    validate: suspend TurbineTestContext<S>.() -> Unit,
) {
    viewModel.state.test(timeout, name) {
        advanceUntilIdle()
        validate()
    }
}
