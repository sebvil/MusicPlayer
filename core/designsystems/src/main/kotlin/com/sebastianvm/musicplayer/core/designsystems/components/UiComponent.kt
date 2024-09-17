package com.sebastianvm.musicplayer.core.designsystems.components

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface UiComponent {
    @Composable fun Content(modifier: Modifier)

    val key: Parcelable
}

interface StateUiComponent : UiComponent, Parcelable {

    override val key: Parcelable
        get() = this
}
