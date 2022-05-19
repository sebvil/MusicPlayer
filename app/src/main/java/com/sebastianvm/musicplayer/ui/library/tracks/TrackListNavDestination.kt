package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.openSortBottomSheet
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import kotlinx.parcelize.Parcelize

@kotlinx.serialization.Serializable
@Parcelize
data class TrackListArguments(val trackListId: Long, val trackListType: TrackListType) :
    NavigationArguments

fun NavGraphBuilder.trackListNavDestination(
    navigationDelegate: NavigationDelegate,
    navController: NavController
) {
    screenDestination<TrackListViewModel>(NavigationRoute.TrackList) { viewModel ->
        TrackListScreen(
            screenViewModel = viewModel,
            navigationDelegate = navigationDelegate,
            object : TrackListScreenNavigationDelegate {

                override fun openSortMenu(mediaId: Long) {
                    navController.openSortBottomSheet(
                        listType = SortableListType.TRACKS,
                        mediaId = mediaId
                    )
                }

                override fun openContextMenu(
                    mediaId: Long,
                    mediaGroup: MediaGroup,
                    trackIndex: Int
                ) {
                    navController.openContextMenu(
                        mediaType = MediaType.TRACK,
                        mediaId = mediaId,
                        mediaGroup = mediaGroup,
                        trackIndex = trackIndex
                    )
                }
            }
        )
    }
}
