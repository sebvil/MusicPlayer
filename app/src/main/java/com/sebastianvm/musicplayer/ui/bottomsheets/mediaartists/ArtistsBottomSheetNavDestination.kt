package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavArgument
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute
import com.sebastianvm.musicplayer.ui.navigation.navigateTo

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.artistsBottomSheetNavDestination(navController: NavController) {
    bottomSheet(
        route = createNavRoute(
            NavRoutes.MEDIA_ARTISTS,
            NavArgs.MEDIA_ID,
            NavArgs.MEDIA_TYPE,
        ),
        arguments = listOf(
            navArgument(NavArgs.MEDIA_ID) { type = NavType.StringType },
            navArgument(NavArgs.MEDIA_TYPE) { type = NavType.StringType },
        )
    ) {
        val sheetViewModel: ArtistsBottomSheetViewModel = hiltViewModel()
        ArtistsBottomSheet(
            sheetViewModel = sheetViewModel,
            delegate = object : ArtistsBottomSheetNavigationDelegate {
                override fun navigateToArtist(artistId: Long) {
                    NavigationDelegate(navController).navigateToScreen(
                        NavigationDestination.ArtistDestination(
                            ArtistArguments(artistId = artistId)
                        )
                    )
                }
            }
        )
    }
}

fun NavController.navigateToArtistsBottomSheet(mediaId: Long, mediaType: MediaType) {
    navigateTo(
        NavRoutes.MEDIA_ARTISTS,
        NavArgument(NavArgs.MEDIA_ID, mediaId),
        NavArgument(NavArgs.MEDIA_TYPE, mediaType.name)
    )
}
