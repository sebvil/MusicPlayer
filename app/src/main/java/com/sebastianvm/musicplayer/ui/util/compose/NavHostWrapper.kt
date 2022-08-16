package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.sebastianvm.musicplayer.ui.components.M3ModalBottomSheetLayout
import com.sebastianvm.musicplayer.ui.theme.AppTheme
import com.sebastianvm.musicplayer.ui.theme.M3AppTheme

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun NavHostWrapper(
    navHost: @Composable (NavHostController) -> Unit,
) {
    AppTheme {
        M3AppTheme {
            val bottomSheetNavigator = rememberBottomSheetNavigator()
            val navController = rememberNavController(bottomSheetNavigator)

//            // TODO: wait until this is a bit more mature
//            val systemUiController = rememberSystemUiController()
//            val surfaceColor = MaterialTheme.colorScheme.surface
//
//            SideEffect {
//                systemUiController.setStatusBarColor(surfaceColor)
//            }

            M3ModalBottomSheetLayout(
                bottomSheetNavigator = bottomSheetNavigator,
                sheetShape = RoundedCornerShape(
                    topStart = AppDimensions.bottomSheet.cornerRadius,
                    topEnd = AppDimensions.bottomSheet.cornerRadius
                )
            ) {
                navHost(navController)
            }
        }
    }
}
