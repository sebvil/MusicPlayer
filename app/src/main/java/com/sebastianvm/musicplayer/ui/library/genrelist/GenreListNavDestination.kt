package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination

fun NavGraphBuilder.genreListNavDestination(
    navigationDelegate: NavigationDelegate,
    navController: NavController
) {
    screenDestination<GenreListViewModel>(NavigationRoute.GenresRoot) { viewModel ->
        GenreListScreen(
            viewModel,
            navigationDelegate = navigationDelegate,
            object : GenreListScreenNavigationDelegate {
                override fun openContextMenu(genreId: Long) {
                    navController.openContextMenu(
                        mediaType = MediaType.GENRE,
                        mediaId = genreId,
                        mediaGroup = MediaGroup(MediaGroupType.GENRE, genreId),
                    )
                }

            })
    }
}
