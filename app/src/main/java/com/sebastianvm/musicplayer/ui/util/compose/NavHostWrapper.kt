package com.sebastianvm.musicplayer.ui.util.compose

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.LocalImageLoader
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.sebastianvm.musicplayer.ui.components.M3ModalBottomSheetLayout
import com.sebastianvm.musicplayer.ui.theme.AppTheme
import com.sebastianvm.musicplayer.ui.theme.M3AppTheme
import com.sebastianvm.musicplayer.ui.util.images.ThumbnailFetcher

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun NavHostWrapper(
    thumbnailFetcher: ThumbnailFetcher? = null,
    navHost: @Composable (NavHostController) -> Unit,
) {
    CompositionLocalProvider(
        LocalImageLoader provides ImageLoader.Builder(LocalContext.current).componentRegistry {
            thumbnailFetcher?.also { add(it) }
        }.build()
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
}
