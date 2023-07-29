package com.sebastianvm.musicplayer.ui.navigation

fun interface NavFunction<T> {
    fun navigate(args: T)

    operator fun invoke(args: T) = navigate(args)
}


fun interface NoArgNavFunction {
    fun navigate()

    operator fun invoke() = navigate()
}



