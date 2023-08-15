package com.sebastianvm.musicplayer.ui.components

import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout

// /**
// * This is only used for previews as of right now
// */
// @Composable
// @ExperimentalMaterialApi
// fun M3ModalBottomSheetLayout(
//    sheetContent: @Composable ColumnScope.() -> Unit,
//    modifier: Modifier = Modifier,
//    sheetState: ModalBottomSheetState =
//        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
//    sheetShape: Shape = androidx.compose.material.MaterialTheme.shapes.large,
//    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
//    sheetBackgroundColor: Color = MaterialTheme.colorScheme.surface,
//    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
//    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
//    content: @Composable () -> Unit
// ) {
//    CompositionLocalProvider(
//        LocalContentColor provides sheetContentColor
//    ) {
//        androidx.compose.material.ModalBottomSheetLayout(
//            sheetContent = sheetContent,
//            modifier = modifier,
//            sheetState = sheetState,
//            sheetShape = sheetShape,
//            sheetElevation = sheetElevation,
//            sheetBackgroundColor = sheetBackgroundColor,
//            sheetContentColor = sheetContentColor,
//            scrimColor = scrimColor,
//            content = content
//        )
//    }
// }

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterialNavigationApi
@Composable
fun M3ModalBottomSheetLayout(
    bottomSheetNavigator: BottomSheetNavigator,
    modifier: Modifier = Modifier,
    sheetShape: Shape = BottomSheetDefaults.ExpandedShape,
    sheetElevation: Dp = BottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = BottomSheetDefaults.ContainerColor,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalContentColor provides sheetContentColor
    ) {
        ModalBottomSheetLayout(
            bottomSheetNavigator = bottomSheetNavigator,
            modifier = modifier,
            sheetShape = sheetShape,
            sheetElevation = sheetElevation,
            sheetBackgroundColor = sheetBackgroundColor,
            sheetContentColor = sheetContentColor,
            scrimColor = scrimColor,
            content = content
        )
    }
}
