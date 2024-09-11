package com.sebastianvm.musicplayer.core.uitest.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.NoArguments

data class FakeMvvmComponent(override val arguments: Arguments = NoArguments) :
    MvvmComponent<Nothing, Nothing, Nothing>() {

    override val viewModel: Nothing
        get() = error("Should not be accessed")

    @Composable
    override fun Content(state: Nothing, handle: Handler<Nothing>, modifier: Modifier) = Unit
}
