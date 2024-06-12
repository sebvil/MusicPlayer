package com.sebastianvm.musicplayer.features.artistsmenu

import androidx.compose.runtime.collectAsState
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.sebastianvm.musicplayer.designsystem.components.ArtistRow
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.player.HasArtists
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

data class ArtistsMenuArguments(val media: HasArtists) : Arguments

data class ArtistsMenuState(val artists: List<ArtistRow.State>) : State

sealed interface ArtistsMenuUserAction : UserAction {
    data class ArtistClicked(val artistId: Long) : ArtistsMenuUserAction
}

class ArtistsMenuStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    arguments: ArtistsMenuArguments,
    artistRepository: ArtistRepository,
    private val navController: NavController,
    recompositionMode: RecompositionMode = RecompositionMode.ContextClock,
) : StateHolder<UiState<ArtistsMenuState>, ArtistsMenuUserAction> {

    override val state: StateFlow<UiState<ArtistsMenuState>> =
        stateHolderScope.launchMolecule(recompositionMode) {
            val artists =
                artistRepository
                    .getArtistsForMedia(arguments.media)
                    .collectAsState(initial = null)
                    .value
            if (artists == null) {
                Loading
            } else {
                Data(
                    ArtistsMenuState(
                        artists = artists.map { artist -> ArtistRow.State.fromArtist(artist) }
                    )
                )
            }
        }

    override fun handle(action: ArtistsMenuUserAction) {
        when (action) {
            is ArtistsMenuUserAction.ArtistClicked -> {
                navController.push(
                    ArtistUiComponent(
                        arguments = ArtistArguments(artistId = action.artistId),
                        navController = navController,
                    ),
                    navOptions = NavOptions(popCurrent = true),
                )
            }
        }
    }
}
