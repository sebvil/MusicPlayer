package com.sebastianvm.musicplayer.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.di.AppDependencies
import com.sebastianvm.musicplayer.di.dependencies
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState
import kotlinx.coroutines.cancel

interface UiComponent<Args : Arguments, SH : StateHolder<*, *>> {

    val arguments: Args
    val key: Any

    fun createStateHolder(dependencies: AppDependencies): SH

    @Composable fun Content(modifier: Modifier)

    fun onCleared() = Unit
}

abstract class BaseUiComponent<
    Args : Arguments,
    S : State,
    UA : UserAction,
    SH : StateHolder<S, UA>,
> : UiComponent<Args, SH> {
    private var stateHolder: SH? = null

    private fun getOrCreateStateHolder(dependencies: AppDependencies): SH {
        return stateHolder ?: createStateHolder(dependencies).also { stateHolder = it }
    }

    @Composable abstract fun Content(state: S, handle: Handler<UA>, modifier: Modifier)

    override val key = this.toString()

    @Composable
    final override fun Content(modifier: Modifier) {
        val dependencies = dependencies()

        val stateHolder = remember { getOrCreateStateHolder(dependencies) }
        val state by stateHolder.currentState
        Content(state = state, handle = stateHolder::handle, modifier = modifier)
    }

    override fun onCleared() {
        super.onCleared()
        stateHolder?.stateHolderScope?.cancel(message = "Screen cleared")
    }
}
