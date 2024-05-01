package com.sebastianvm.musicplayer.features.genre.menu

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolderFactory

@Composable
fun genreContextMenuStateHolderFactory(): StateHolderFactory<GenreContextMenuArguments, GenreContextMenuStateHolder> {
    return stateHolderFactory { dependencyContainer, args ->
        GenreContextMenuStateHolder(
            arguments = args,
            genreRepository = dependencyContainer.repositoryProvider.genreRepository,
            delegate = object : GenreContextMenuDelegate {
                override fun showGenre(genreId: Long) {
                    TODO("navigation")
//                    navigator.navigate(
//                        TrackListRouteDestination(
//                            TrackListArgumentsForNav(
//                                trackListType = MediaGroup.Genre(genreId)
//                            )
//                        )
//                    )
                }
            }
        )
    }
}