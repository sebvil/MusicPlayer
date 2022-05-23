package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ArtistsMenuArguments(val mediaId: Long, val mediaType: MediaType) : NavigationArguments

fun NavGraphBuilder.artistsBottomSheetNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<ArtistsBottomSheetViewModel>(
        destination = NavigationRoute.ArtistsMenu,
        destinationType = DestinationType.BottomSheet
    ) { viewModel ->
        ArtistsBottomSheet(
            sheetViewModel = viewModel,
            navigationDelegate = navigationDelegate
        )
    }
}
