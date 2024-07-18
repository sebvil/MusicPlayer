package com.sebastianvm.musicplayer.services.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.sebastianvm.musicplayer.services.HasServices
import com.sebastianvm.musicplayer.services.Services
import com.sebastianvm.musicplayer.services.features.mvvm.Arguments
import com.sebastianvm.musicplayer.services.features.mvvm.Handler
import com.sebastianvm.musicplayer.services.features.mvvm.State
import com.sebastianvm.musicplayer.services.features.mvvm.StateHolder
import com.sebastianvm.musicplayer.services.features.mvvm.UserAction
import com.sebastianvm.musicplayer.services.features.mvvm.currentState
import kotlinx.coroutines.cancel

interface UiComponent<Args : Arguments, SH : StateHolder<*, *>> {

    val arguments: Args
    val key: Any

    fun createStateHolder(services: Services): SH

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

    private fun getOrCreateStateHolder(services: Services): SH {
        return stateHolder ?: createStateHolder(services).also { stateHolder = it }
    }

    @Composable abstract fun Content(state: S, handle: Handler<UA>, modifier: Modifier)

    override val key = this.toString()

    @Composable
    final override fun Content(modifier: Modifier) {
        val dependencies = services()

        val stateHolder = remember { getOrCreateStateHolder(dependencies) }
        val state by stateHolder.currentState
        Content(state = state, handle = stateHolder::handle, modifier = modifier)
    }

    override fun onCleared() {
        super.onCleared()
        stateHolder?.stateHolderScope?.cancel(message = "Screen cleared")
    }
}

@Composable
fun services(): Services = (LocalContext.current.applicationContext as HasServices).services
