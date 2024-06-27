package com.sebastianvm.musicplayer.designsystem.icons

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource

sealed interface IconState {
    @Composable fun icon(): ImageVector
}

@JvmInline
value class ResourceIcon(@DrawableRes val resId: Int) : IconState {
    @Composable override fun icon(): ImageVector = ImageVector.vectorResource(id = resId)
}

@JvmInline
value class VectorIcon(val imageVector: ImageVector) : IconState {
    @Composable override fun icon(): ImageVector = imageVector
}

@Composable fun IconState.painter() = rememberVectorPainter(image = icon())
