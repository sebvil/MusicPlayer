package com.sebastianvm.musicplayer.features.track.menu

import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sebastianvm.musicplayer.destinations.ArtistRouteDestination
import com.sebastianvm.musicplayer.destinations.ArtistsBottomSheetDestination
import com.sebastianvm.musicplayer.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.track.list.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.model.MediaWithArtists
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists.ArtistsMenuArguments
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory

class TrackContextMenuStateHolderFactory(
    private val dependencyContainer: DependencyContainer,
    private val navigator: DestinationsNavigator,
) : StateHolderFactory<TrackContextMenuArguments, TrackContextMenuStateHolder> {

    override fun getStateHolder(arguments: TrackContextMenuArguments): TrackContextMenuStateHolder {
        return TrackContextMenuStateHolder(
            arguments = arguments,
            trackRepository = dependencyContainer.repositoryProvider.trackRepository,
            playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
            playbackManager = dependencyContainer.repositoryProvider.playbackManager,
            delegate = object : TrackContextMenuDelegate {
                override fun showAlbum(albumId: Long) {
                    navigator.navigate(
                        TrackListRouteDestination(
                            TrackListArgumentsForNav(
                                trackListType = MediaGroup.Album(albumId)
                            )
                        )
                    )
                }

                override fun showArtists(trackId: Long) {
                    navigator.navigate(
                        ArtistsBottomSheetDestination(
                            ArtistsMenuArguments(MediaWithArtists.Track, trackId)
                        )
                    )
                }

                override fun showArtist(artistId: Long) {
                    navigator.navigate(ArtistRouteDestination(ArtistArguments(artistId)))
                }
            }
        )
    }
}
