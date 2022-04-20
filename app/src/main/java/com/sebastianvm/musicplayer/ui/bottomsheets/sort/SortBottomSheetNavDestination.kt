package com.sebastianvm.musicplayer.ui.bottomsheets.sort

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavArgument
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute
import com.sebastianvm.musicplayer.ui.navigation.navigateTo

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.sortBottomSheetNavDestination(navController: NavController) {
    bottomSheet(
        route = createNavRoute(NavRoutes.SORT, NavArgs.SORTABLE_LIST_TYPE, NavArgs.MEDIA_ID),
        arguments = listOf(
            navArgument(NavArgs.SORTABLE_LIST_TYPE) { type = NavType.StringType },
            navArgument(NavArgs.MEDIA_ID) { type = NavType.StringType },
        )
    ) {
        val sheetViewModel = hiltViewModel<SortBottomSheetViewModel>()
        SortBottomSheet(
            sheetViewModel = sheetViewModel,
            delegate = object : SortBottomSheetDelegate {
                override fun popBackStack() {
                    navController.popBackStack()
                }
            }
        )
    }
}

fun NavController.openSortBottomSheet(listType: SortableListType, mediaId: String = "") {
    navigateTo(
        NavRoutes.SORT,
        NavArgument(NavArgs.SORTABLE_LIST_TYPE, listType),
        NavArgument(NavArgs.MEDIA_ID, mediaId)
    )
}