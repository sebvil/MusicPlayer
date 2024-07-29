package com.sebastianvm.musicplayer.core.uitest.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent

data class FakeUiComponent(val arguments: Any?, val name: String) : UiComponent<FakeStateHolder> {

    override fun createStateHolder(services: Services): FakeStateHolder {
        return FakeStateHolder()
    }

    @Composable override fun Content(modifier: Modifier) = Unit
}
