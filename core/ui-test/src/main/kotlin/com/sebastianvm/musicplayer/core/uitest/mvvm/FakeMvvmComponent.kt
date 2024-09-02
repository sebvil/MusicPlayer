package com.sebastianvm.musicplayer.core.uitest.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent

data class FakeMvvmComponent(val arguments: Any? = null, val name: String = "fake component") :
    MvvmComponent {

    @Composable override fun Content(modifier: Modifier) = Unit
}
