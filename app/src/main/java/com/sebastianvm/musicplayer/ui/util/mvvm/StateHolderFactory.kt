package com.sebastianvm.musicplayer.ui.util.mvvm

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.di.dependencies
import org.checkerframework.checker.units.qual.A

fun interface StateHolderFactory<A : Arguments, D : Delegate, S : StateHolder<*, *>> {
    fun getStateHolder(arguments: A, delegate: D): S
}

fun <Args : Arguments, S : StateHolder<*, *>> StateHolderFactory<Args, NoDelegate, S>.getStateHolder(
    arguments: Args
) = getStateHolder(arguments, NoDelegate)

@Composable
fun <Args : Arguments, D : Delegate, S : StateHolder<*, *>> stateHolderFactory(
    factory: (DependencyContainer, Args, D) -> S
): StateHolderFactory<Args, D, S> {
    val dependencies = dependencies()
    return StateHolderFactory { arguments, delegate -> factory(dependencies, arguments, delegate) }
}

@Composable
fun <Args : Arguments, S : StateHolder<*, *>> stateHolderFactory(
    factory: (DependencyContainer, Args) -> S
): StateHolderFactory<Args, NoDelegate, S> {
    val dependencies = dependencies()
    return StateHolderFactory { arguments, _ -> factory(dependencies, arguments) }
}
