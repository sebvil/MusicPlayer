package com.sebastianvm.musicplayer.features.artistsmenu

import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.player.HasArtists
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ArtistsMenuArguments(val media: HasArtists) : Arguments

data class ArtistsMenuState(val modelListState: ModelListState) : State

sealed interface ArtistsMenuUserAction : UserAction {
    data class ArtistClicked(val artistId: Long) : ArtistsMenuUserAction
}

class ArtistsMenuStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    arguments: ArtistsMenuArguments,
    artistRepository: ArtistRepository,
    private val navController: NavController,
) : StateHolder<UiState<ArtistsMenuState>, ArtistsMenuUserAction> {

    override val state: StateFlow<UiState<ArtistsMenuState>> =
        artistRepository.getArtistsForMedia(arguments.media)
            .map { artists ->
                Data(
                    ArtistsMenuState(
                        modelListState = ModelListState(
                            items = artists.map { artist ->
                                artist.toModelListItemState(
                                    trailingButtonType = null
                                )
                            }
                        )
                    )
                )
            }.stateIn(stateHolderScope, Lazily, Loading)

    override fun handle(action: ArtistsMenuUserAction) {
        when (action) {
            is ArtistsMenuUserAction.ArtistClicked -> {
                navController.push(
                    ArtistUiComponent(
                        arguments = ArtistArguments(artistId = action.artistId),
                        navController = navController
                    ),
                    navOptions = NavOptions(popCurrent = true)
                )
            }
        }
    }
}

fun getArtistsMenuStateHolder(
    dependencies: DependencyContainer,
    arguments: ArtistsMenuArguments,
    navController: NavController
): ArtistsMenuStateHolder {
    return ArtistsMenuStateHolder(
        arguments = arguments,
        artistRepository = dependencies.repositoryProvider.artistRepository,
        navController = navController
    )
}
