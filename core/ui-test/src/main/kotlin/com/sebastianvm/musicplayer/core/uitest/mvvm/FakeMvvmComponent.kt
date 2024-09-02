package com.sebastianvm.musicplayer.core.uitest.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent

data class FakeMvvmComponent(val arguments: Any?, val name: String) :
    MvvmComponent<FakeStateHolder> {

    override val viewModel: FakeViewModel by lazy {
        FakeViewModel()
    }

    @Composable
    override fun Content(modifier: Modifier) = Unit
}
