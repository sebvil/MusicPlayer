package com.sebastianvm.musicplayer.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.sebastianvm.musicplayer.core.services.HasServices
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.currentState
import kotlinx.coroutines.cancel

interface UiComponent<SH : StateHolder<*, *>> {

    val key: Any

    fun createStateHolder(
        services: Services,
    ): SH

    @Composable fun Content(modifier: Modifier)

    fun onCleared() = Unit
}

abstract class BaseUiComponent<
    S : State,
    UA : UserAction,
    SH : StateHolder<S, UA>,
> : UiComponent<SH> {
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
