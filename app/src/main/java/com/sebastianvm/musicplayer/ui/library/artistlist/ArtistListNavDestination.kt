package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.artist.navigateToArtist
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination

fun NavGraphBuilder.artistListNavDestination(
    navigationDelegate: NavigationDelegate,
    navController: NavController
) {
    screenDestination<ArtistListViewModel>(NavigationRoute.ArtistsRoot) { viewModel ->
        ArtistListScreen(
            viewModel,
            navigationDelegate = navigationDelegate,
            object : ArtistListScreenNavigationDelegate {
                override fun navigateToArtist(artistId: Long) {
                    navController.navigateToArtist(artistId)
                }

                override fun openContextMenu(artistId: Long) {
                    navController.openContextMenu(
                        MediaType.ARTIST,
                        artistId,
                        MediaGroup(MediaGroupType.ARTIST, mediaId = artistId),
                    )
                }
            })
    }

}
