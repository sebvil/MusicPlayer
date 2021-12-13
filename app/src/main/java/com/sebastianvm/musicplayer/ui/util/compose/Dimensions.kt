package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val xxSmall: Dp = 2.dp,
    val xSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val mediumSmall: Dp = 12.dp,
    val medium: Dp = 16.dp,
    val mediumLarge: Dp = 20.dp,
    val large: Dp = 24.dp,
    val xLarge: Dp = 28.dp,
    val xxLarge: Dp = 32.dp,
)

data class BottomSheet(
    val rowHeight: Dp = 56.dp,
    val startPadding: Dp = 16.dp,
    val cornerRadius: Dp = 16.dp
)

data class AlbumRowDimensions(
    val imageSize: Dp = 56.dp,
    val height: Dp = 72.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
val LocalBottomSheetDimensions = staticCompositionLocalOf { BottomSheet() }
val LocalAlbumRowDimensions = staticCompositionLocalOf { AlbumRowDimensions() }


object AppDimensions {
    val spacing: Spacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current

    val bottomSheet: BottomSheet
        @Composable
        @ReadOnlyComposable
        get() = LocalBottomSheetDimensions.current

    val albumRowDimensions: AlbumRowDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalAlbumRowDimensions.current
}