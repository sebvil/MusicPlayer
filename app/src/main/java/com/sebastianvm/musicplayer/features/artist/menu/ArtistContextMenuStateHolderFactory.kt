package com.sebastianvm.musicplayer.features.artist.menu

import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sebastianvm.musicplayer.destinations.ArtistRouteDestination
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory

class ArtistContextMenuStateHolderFactory(
    private val dependencyContainer: DependencyContainer,
    private val navigator: DestinationsNavigator,
) : StateHolderFactory<ArtistContextMenuArguments, ArtistContextMenuStateHolder> {

    override fun getStateHolder(arguments: ArtistContextMenuArguments): ArtistContextMenuStateHolder {
        return ArtistContextMenuStateHolder(
            arguments = arguments,
            artistRepository = dependencyContainer.repositoryProvider.artistRepository,
            delegate = object : ArtistContextMenuDelegate {
                override fun showArtist(artistId: Long) {
                    navigator.navigate(ArtistRouteDestination(ArtistArguments(artistId)))
                }
            }
        )
    }
}
