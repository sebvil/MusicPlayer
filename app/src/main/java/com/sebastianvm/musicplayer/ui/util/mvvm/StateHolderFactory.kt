package com.sebastianvm.musicplayer.ui.util.mvvm

interface StateHolderFactory<A : Arguments, S : StateHolder<*, *>> {
    fun getStateHolder(arguments: A): S
}
