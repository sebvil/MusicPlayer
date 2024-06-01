package com.sebastianvm.musicplayer.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.di.dependencies
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import kotlinx.coroutines.cancel

interface UiComponent<Args : Arguments, SH : StateHolder<*, *>> {

    val arguments: Args
    val key: Any

    fun createStateHolder(dependencies: DependencyContainer): SH

    @Composable
    fun Content(modifier: Modifier)

    fun onCleared() = Unit
}

abstract class BaseUiComponent<Args : Arguments, SH : StateHolder<*, *>> : UiComponent<Args, SH> {
    private var stateHolder: SH? = null

    private fun getOrCreateStateHolder(dependencies: DependencyContainer): SH {
        return stateHolder ?: createStateHolder(dependencies).also { stateHolder = it }
    }

    @Composable
    abstract fun Content(stateHolder: SH, modifier: Modifier)

    override val key = this.toString()

    @Composable
    final override fun Content(modifier: Modifier) {
        val dependencies = dependencies()

        Content(
            stateHolder = remember {
                getOrCreateStateHolder(dependencies)
            },
            modifier = modifier
        )
    }

    override fun onCleared() {
        super.onCleared()
        stateHolder?.stateHolderScope?.cancel(message = "Screen cleared")
    }
}
