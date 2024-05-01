package com.sebastianvm.musicplayer.features.artist.menu

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolderFactory

@Composable
fun artistContextMenuStateHolderFactory(): StateHolderFactory<ArtistContextMenuArguments, ArtistContextMenuStateHolder> {
    return stateHolderFactory { dependencyContainer, args ->
        ArtistContextMenuStateHolder(
            arguments = args,
            artistRepository = dependencyContainer.repositoryProvider.artistRepository,
            delegate = object : ArtistContextMenuDelegate {
                override fun showArtist(artistId: Long) {
                    TODO("navigation")
//                    navigator.navigate(ArtistRouteDestination(ArtistArguments(artistId)))
                }
            }
        )
    }
}
