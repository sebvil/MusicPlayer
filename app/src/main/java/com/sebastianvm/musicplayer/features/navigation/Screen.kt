package com.sebastianvm.musicplayer.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.di.dependencies
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import kotlinx.coroutines.cancel

interface Screen<Args : Arguments, SH : StateHolder<*, *>> {

    val arguments: Args

    fun createStateHolder(dependencies: DependencyContainer): SH

    @Composable
    fun Content(saveableStateHolder: SaveableStateHolder, modifier: Modifier)

    fun onCleared() = Unit
}

abstract class BaseScreen<Args : Arguments, SH : StateHolder<*, *>> : Screen<Args, SH> {
    private var stateHolder: SH? = null

    private fun getOrCreateStateHolder(dependencies: DependencyContainer): SH {
        return stateHolder ?: createStateHolder(dependencies).also { stateHolder = it }
    }

    @Composable
    abstract fun Content(stateHolder: SH, modifier: Modifier)

    private var _saveableStateHolder: SaveableStateHolder? = null
    private val key
        get() = this.toString()

    @Composable
    final override fun Content(saveableStateHolder: SaveableStateHolder, modifier: Modifier) {
        _saveableStateHolder = saveableStateHolder
        val dependencies = dependencies()
        saveableStateHolder.SaveableStateProvider(key) {
            Content(
                stateHolder = remember {
                    getOrCreateStateHolder(dependencies)
                },
                modifier = modifier
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        stateHolder?.stateHolderScope?.cancel(message = "Screen cleared")
        _saveableStateHolder?.removeState(key)
        _saveableStateHolder = null
    }
}
