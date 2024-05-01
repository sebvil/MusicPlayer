package com.sebastianvm.musicplayer.features.playlist.menu

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolderFactory

@Composable
fun playlistContextMenuStateHolderFactory(): StateHolderFactory<PlaylistContextMenuArguments, PlaylistContextMenuStateHolder> {
    return stateHolderFactory { dependencyContainer, args ->
        PlaylistContextMenuStateHolder(
            arguments = args,
            playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
            delegate = object : PlaylistContextMenuDelegate {
                override fun showPlaylist(playlistId: Long) {
                    TODO("navigation")
//                    navigator.navigate(
//                        com.sebastianvm.musicplayer.destinations.TrackListRouteDestination(
//                            TrackListArgumentsForNav(MediaGroup.Playlist(playlistId))
//                        )
//                    )
                }
            }
        )
    }
}
