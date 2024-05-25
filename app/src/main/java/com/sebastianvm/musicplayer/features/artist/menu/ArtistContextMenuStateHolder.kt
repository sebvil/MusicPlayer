package com.sebastianvm.musicplayer.features.artist.menu

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistScreen
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.rememberStateHolder
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ArtistContextMenuArguments(val artistId: Long) : Arguments

sealed interface ArtistContextMenuState : State {
    data class Data(
        val artistName: String,
        val artistId: Long,
    ) : ArtistContextMenuState

    data object Loading : ArtistContextMenuState
}

sealed interface ArtistContextMenuUserAction : UserAction {
    data object PlayArtistClicked : ArtistContextMenuUserAction
    data object ViewArtistClicked : ArtistContextMenuUserAction
}

class ArtistContextMenuStateHolder(
    arguments: ArtistContextMenuArguments,
    artistRepository: ArtistRepository,
    private val navController: NavController,
    stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<ArtistContextMenuState, ArtistContextMenuUserAction> {

    private val artistId = arguments.artistId

    override val state: StateFlow<ArtistContextMenuState> =
        artistRepository.getArtist(artistId).map { artist ->
            ArtistContextMenuState.Data(
                artistName = artist.artist.artistName,
                artistId = artistId,
            )
        }.stateIn(stateHolderScope, SharingStarted.Lazily, ArtistContextMenuState.Loading)

    override fun handle(action: ArtistContextMenuUserAction) {
        when (action) {
            ArtistContextMenuUserAction.PlayArtistClicked -> {
            }

            ArtistContextMenuUserAction.ViewArtistClicked -> {
                navController.push(
                    ArtistScreen(
                        arguments = ArtistArguments(artistId = artistId),
                        navController = navController
                    ),
                    navOptions = NavOptions(popCurrent = true)
                )
            }
        }
    }
}

@Composable
fun rememberArtistContextMenuStateHolder(
    arguments: ArtistContextMenuArguments,
    navController: NavController
): ArtistContextMenuStateHolder {
    return rememberStateHolder { dependencyContainer ->
        ArtistContextMenuStateHolder(
            arguments = arguments,
            artistRepository = dependencyContainer.repositoryProvider.artistRepository,
            navController = navController,
        )
    }
}
