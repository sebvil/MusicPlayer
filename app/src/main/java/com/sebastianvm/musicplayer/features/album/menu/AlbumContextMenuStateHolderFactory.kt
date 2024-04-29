package com.sebastianvm.musicplayer.features.album.menu

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

class AlbumContextMenuStateHolderFactory(
    private val dependencyContainer: DependencyContainer,
    private val navigator: DestinationsNavigator,
) : StateHolderFactory<AlbumContextMenuArguments, AlbumContextMenuStateHolder> {

    override fun getStateHolder(arguments: AlbumContextMenuArguments): AlbumContextMenuStateHolder {
        return AlbumContextMenuStateHolder(
            arguments = arguments,
            albumRepository = dependencyContainer.repositoryProvider.albumRepository,
            trackRepository = dependencyContainer.repositoryProvider.trackRepository,
            playbackManager = dependencyContainer.repositoryProvider.playbackManager,
            delegate = object : AlbumContextMenuDelegate {
                override fun showAlbum(albumId: Long) {
                    navigator.navigate(
                        TrackListRouteDestination(
                            TrackListArgumentsForNav(MediaGroup.Album(albumId))
                        )
                    )
                }

                override fun showArtists(albumId: Long) {
                    navigator.navigate(
                        ArtistsBottomSheetDestination(
                            ArtistsMenuArguments(MediaWithArtists.Album, albumId)
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
