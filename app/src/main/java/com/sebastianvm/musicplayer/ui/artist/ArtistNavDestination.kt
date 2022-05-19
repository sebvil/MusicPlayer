package com.sebastianvm.musicplayer.ui.artist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
data class ArtistArguments(val artistId: Long) : NavigationArguments

fun NavGraphBuilder.artistNavDestination(
    navigationDelegate: NavigationDelegate,
    navController: NavController
) {
    screenDestination<ArtistViewModel>(NavigationRoute.Artist) { viewModel ->
        ArtistScreen(
            viewModel,
            navigationDelegate = navigationDelegate,
            delegate = object : ArtistScreenNavigationDelegate {
                override fun openContextMenu(albumId: Long) {
                    navController.openContextMenu(
                        mediaType = MediaType.ALBUM,
                        mediaId = albumId,
                        mediaGroup = MediaGroup(MediaGroupType.ALBUM, albumId),
                    )
                }
            })
    }
}

