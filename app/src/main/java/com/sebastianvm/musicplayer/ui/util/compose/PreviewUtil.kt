package com.sebastianvm.musicplayer.ui.util.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.sebastianvm.musicplayer.ui.components.M3ModalBottomSheetLayout
import com.sebastianvm.musicplayer.ui.navigation.BottomNavBar
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.theme.AppTheme
import com.sebastianvm.musicplayer.ui.theme.M3AppTheme


@Composable
fun ThemedPreview(
    getBackgroundColor: @Composable () -> Color = { MaterialTheme.colorScheme.background },
    content: @Composable () -> Unit
) {
    AppTheme {
        M3AppTheme {
            Surface(
                color = getBackgroundColor()
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenPreview(
    topBar: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    screen: @Composable () -> Unit
) {
    NavHostWrapper { navController ->
        Scaffold(
            topBar = topBar,
            bottomBar = { BottomNavBar(NavigationDelegate(navController)) },
            floatingActionButton = fab
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

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class ComponentPreview