//package com.sebastianvm.musicplayer.ui.components
//
//import androidx.compose.foundation.layout.ColumnScope
//import androidx.compose.material.ExperimentalMaterialApi
//import androidx.compose.material.ModalBottomSheetDefaults
//import androidx.compose.material.ModalBottomSheetState
//import androidx.compose.material.ModalBottomSheetValue
//import androidx.compose.material.rememberModalBottomSheetState
//import androidx.compose.material3.LocalContentColor
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.contentColorFor
//import androidx.compose.material3.surfaceColorAtElevation
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.CompositionLocalProvider
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Shape
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import com.google.accompanist.navigation.material.BottomSheetNavigator
//import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
//import com.google.accompanist.navigation.material.ModalBottomSheetLayout
//
//
///**
// * This is only used for previews as of right now
// */
//@Composable
//@ExperimentalMaterialApi
//fun M3ModalBottomSheetLayout(
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
//) {
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
//}
//
//@ExperimentalMaterialNavigationApi
//@Composable
//fun M3ModalBottomSheetLayout(
//    bottomSheetNavigator: BottomSheetNavigator,
//    modifier: Modifier = Modifier,
//    sheetShape: Shape = androidx.compose.material.MaterialTheme.shapes.large,
//    sheetElevation: Dp = 4.dp,
//    sheetBackgroundColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(sheetElevation),
//    sheetContentColor: Color = contentColorFor(MaterialTheme.colorScheme.surface),
//    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
//    content: @Composable () -> Unit
//) {
//    CompositionLocalProvider(
//        LocalContentColor provides sheetContentColor
//    ) {
//        ModalBottomSheetLayout(
//            bottomSheetNavigator = bottomSheetNavigator,
//            modifier = modifier,
//            sheetShape = sheetShape,
//            sheetElevation = sheetElevation,
//            sheetBackgroundColor = sheetBackgroundColor,
//            sheetContentColor = sheetContentColor,
//            scrimColor = scrimColor,
//            content = content
//        )
//    }
//}
//
