package com.sebastianvm.musicplayer.features.playlist.menu

import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sebastianvm.musicplayer.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.track.list.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory

class PlaylistContextMenuStateHolderFactory(
    private val dependencyContainer: DependencyContainer,
    private val navigator: DestinationsNavigator,
) : StateHolderFactory<PlaylistContextMenuArguments, PlaylistContextMenuStateHolder> {

    override fun getStateHolder(arguments: PlaylistContextMenuArguments): PlaylistContextMenuStateHolder {
        return PlaylistContextMenuStateHolder(
            arguments = arguments,
            playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
            delegate = object : PlaylistContextMenuDelegate {
                override fun showPlaylist(playlistId: Long) {
                    navigator.navigate(
                        TrackListRouteDestination(
                            TrackListArgumentsForNav(MediaGroup.Playlist(playlistId))
                        )
                    )
                }
            }
        )
    }
}
