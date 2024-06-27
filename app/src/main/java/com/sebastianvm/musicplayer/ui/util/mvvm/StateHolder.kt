package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface StateHolder<S : State, A : UserAction> {
    val stateHolderScope: CoroutineScope
    val state: StateFlow<S>

    fun handle(action: A)
}

abstract class BaseStateHolder<S : State, A : UserAction>(
    final override val stateHolderScope: CoroutineScope = stateHolderScope(),
    recompositionMode: RecompositionMode = RecompositionMode.ContextClock
) : StateHolder<S, A> {

    final override val state: StateFlow<S> by lazy {
        stateHolderScope.launchMolecule(recompositionMode) { presenter() }
    }

    @Composable protected abstract fun presenter(): S
}

val <S : State, A : UserAction> StateHolder<S, A>.currentState: androidx.compose.runtime.State<S>
    @Composable get() = state.collectAsStateWithLifecycle()
