package com.sebastianvm.musicplayer.ui.util.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sebastianvm.musicplayer.ui.components.M3ModalBottomSheetLayout
import com.sebastianvm.musicplayer.ui.navigation.BottomNavBar
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegateImpl
import com.sebastianvm.musicplayer.ui.theme.AppTheme
import com.sebastianvm.musicplayer.ui.theme.M3AppTheme

object PreviewUtil {
    private val strings = listOf(
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

    fun randomString() = strings.random()
}

@Composable
fun ThemedPreview(
    content: @Composable () -> Unit
) {
    AppTheme {
        M3AppTheme {
            Surface {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenPreview(
    screen: @Composable () -> Unit
) {
    NavHostWrapper { navController ->
        Scaffold(
            bottomBar = { BottomNavBar(NavigationDelegateImpl(navController)) },
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                screen()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetPreview(bottomSheet: @Composable () -> Unit) {
    ThemedPreview {
        M3ModalBottomSheetLayout(
            sheetContent = { bottomSheet() },
            sheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded),
            sheetShape = RoundedCornerShape(
                topStart = AppDimensions.bottomSheet.cornerRadius,
                topEnd = AppDimensions.bottomSheet.cornerRadius
            )
        ) {}
    }

}

@Preview(showSystemUi = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true)
annotation class ScreenPreview

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class ComponentPreview