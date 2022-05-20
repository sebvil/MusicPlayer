package com.sebastianvm.musicplayer.ui.bottomsheets.sort

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class SortMenuArguments(val listType: SortableListType, val mediaId: Long = 0) :
    NavigationArguments

fun NavGraphBuilder.sortBottomSheetNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<SortBottomSheetViewModel>(
        NavigationRoute.SortMenu,
        destinationType = DestinationType.BottomSheet
    ) { viewModel ->
        SortBottomSheet(sheetViewModel = viewModel, navigationDelegate = navigationDelegate)
    }
}
