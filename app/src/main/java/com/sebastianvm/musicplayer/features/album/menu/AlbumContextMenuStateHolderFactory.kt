package com.sebastianvm.musicplayer.features.album.menu

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolderFactory

@Composable
fun albumContextMenuStateHolderFactory(): StateHolderFactory<AlbumContextMenuArguments, AlbumContextMenuStateHolder> {
    return stateHolderFactory { dependencyContainer, args ->

        AlbumContextMenuStateHolder(
            arguments = args,
            albumRepository = dependencyContainer.repositoryProvider.albumRepository,
            trackRepository = dependencyContainer.repositoryProvider.trackRepository,
            playbackManager = dependencyContainer.repositoryProvider.playbackManager,
            delegate = object : AlbumContextMenuDelegate {
                override fun showAlbum(albumId: Long) {
                    TODO("navigation")
//                    navigator.navigate(
//                        TrackListRouteDestination(
//                            TrackListArgumentsForNav(MediaGroup.Album(albumId))
//                        )
//                    )
                }

                override fun showArtists(albumId: Long) {
                    TODO("navigation")
//                    navigator.navigate(
//                        ArtistsBottomSheetDestination(
//                            ArtistsMenuArguments(MediaWithArtists.Album, albumId)
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