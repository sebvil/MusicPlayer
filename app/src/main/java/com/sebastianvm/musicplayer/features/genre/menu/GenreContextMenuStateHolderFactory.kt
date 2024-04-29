package com.sebastianvm.musicplayer.features.genre.menu

import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sebastianvm.musicplayer.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.track.list.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory

class GenreContextMenuStateHolderFactory(
    private val dependencyContainer: DependencyContainer,
    private val navigator: DestinationsNavigator,
) : StateHolderFactory<GenreContextMenuArguments, GenreContextMenuStateHolder> {

    override fun getStateHolder(arguments: GenreContextMenuArguments): GenreContextMenuStateHolder {
        return GenreContextMenuStateHolder(
            arguments = arguments,
            genreRepository = dependencyContainer.repositoryProvider.genreRepository,
            delegate = object : GenreContextMenuDelegate {
                override fun showGenre(genreId: Long) {
                    navigator.navigate(
                        TrackListRouteDestination(
                            TrackListArgumentsForNav(
                                trackListType = MediaGroup.Genre(genreId)
                            )
                        )
                    )
                }
            }
        )
    }
}
