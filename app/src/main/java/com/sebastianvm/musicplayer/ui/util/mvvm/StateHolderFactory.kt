package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.di.dependencies
import org.checkerframework.checker.units.qual.A

fun interface StateHolderFactory<A : Arguments, S : StateHolder<*, *>> {
    fun getStateHolder(arguments: A): S
}

@Composable
fun <Args : Arguments, S : StateHolder<*, *>> stateHolderFactory(
    factory: (DependencyContainer, Args) -> S
): StateHolderFactory<Args, S> {
    val dependencies = dependencies()
    return StateHolderFactory { arguments -> factory(dependencies, arguments) }
}