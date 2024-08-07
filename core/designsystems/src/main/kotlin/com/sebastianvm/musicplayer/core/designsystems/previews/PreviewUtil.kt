package com.sebastianvm.musicplayer.core.designsystems.previews

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sebastianvm.musicplayer.core.designsystems.theme.M3AppTheme

object PreviewUtil {
    private val strings =
        listOf(
            "Lorem ipsum dolor sit amet",
            "consectetur adipiscing elit",
            "sed do eiusmod tempor incididunt ut labore",
            "et dolore magna aliqua",
            "Ut enim ad minim veniam",
            "quis nostrud exercitation ullamco laboris nisi ut",
            "aliquip ex ea commodo consequat",
            "Duis aute irure dolor in reprehenderit in voluptate velit esse",
            "cillum dolore eu fugiat nulla pariatur",
            "Excepteur sint occaecat cupidatat non proident",
            "culpa qui officia deserunt mollit anim id est laborum",
            "sunt in",
            "Lorem ipsum dolor sit amet",
            "con elit",
            "sed do et labore",
            "et dolore magna aliqua",
            "Ut eniiam",
            "quis nos ",
            "aliquip ex ea quat",
            "Duis aute irure e",
            "cillumr",
            "Exct",
            "culpa m",
        )

    fun randomString(minLength: Int = 0, maxLength: Int = strings.maxOf { it.length }) =
        strings.filter { it.length in minLength..maxLength }.random()
}

@Composable
fun ThemedPreview(content: @Composable () -> Unit) {
    M3AppTheme { Surface { content() } }
}

@Composable
fun ScreenPreview(screen: @Composable () -> Unit) {
    M3AppTheme { screen() }
}

// @OptIn(ExperimentalMaterialApi::class)
// @Composable
// fun BottomSheetPreview(bottomSheet: @Composable () -> Unit) {
//    ThemedPreview {
//        M3ModalBottomSheetLayout(
//            sheetContent = { bottomSheet() },
//            sheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded),
//            sheetShape = RoundedCornerShape(
//                topStart = AppDimensions.bottomSheet.cornerRadius,
//                topEnd = AppDimensions.bottomSheet.cornerRadius
//            )
//        ) {}
//    }
//
// }

// @Preview(showSystemUi = true)
// @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true)
// @Preview(
//    name = "Landscape",
//    device = "spec:parent=pixel_6,orientation=landscape", showSystemUi = true
// )
// @Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
// annotation class PreviewScreens

@Preview @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES) annotation class PreviewComponents
