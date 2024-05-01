package com.sebastianvm.musicplayer.features.track.menu

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolderFactory


@Composable
fun trackContextMenuStateHolderFactory(): StateHolderFactory<TrackContextMenuArguments, TrackContextMenuStateHolder> {
    return stateHolderFactory { dependencyContainer, arguments ->
        TrackContextMenuStateHolder(
            arguments = arguments,
            trackRepository = dependencyContainer.repositoryProvider.trackRepository,
            playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
            playbackManager = dependencyContainer.repositoryProvider.playbackManager,
            delegate = object : TrackContextMenuDelegate {
                override fun showAlbum(albumId: Long) {
                    TODO("navigation")
//                    navigator.navigate(
//                        com.sebastianvm.musicplayer.destinations.TrackListRouteDestination(
//                            TrackListArgumentsForNav(
//                                trackListType = MediaGroup.Album(albumId)
//                            )
//                        )
//                    )
                }

                override fun showArtists(trackId: Long) {
                    TODO("navigation")
//                    navigator.navigate(
//                        ArtistsBottomSheetDestination(
//                            ArtistsMenuArguments(MediaWithArtists.Track, trackId)
//                        )
//                    )
                }

                override fun showArtist(artistId: Long) {
                    TODO("navigation")
//                    navigator.navigate(ArtistRouteDestination(ArtistArguments(artistId)))
                }
            }
        )
    }
}
