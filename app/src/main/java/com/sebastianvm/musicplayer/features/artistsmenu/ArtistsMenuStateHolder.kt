package com.sebastianvm.musicplayer.features.artistsmenu

import com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
import com.sebastianvm.musicplayer.core.designsystems.components.ArtistRow
import com.sebastianvm.musicplayer.core.model.HasArtists
import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import com.sebastianvm.musicplayer.core.ui.mvvm.Data
import com.sebastianvm.musicplayer.core.ui.mvvm.Loading
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.core.ui.mvvm.UiState
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.stateHolderScope
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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
) : StateHolder<UiState<ArtistsMenuState>, ArtistsMenuUserAction> {

    override val state: StateFlow<UiState<ArtistsMenuState>> =
        artistRepository
            .getArtistsForMedia(arguments.media)
            .map { artists ->
                Data(
                    ArtistsMenuState(
                        artists = artists.map { artist -> ArtistRow.State.fromArtist(artist) }))
            }
            .stateIn(stateHolderScope, Lazily, Loading)

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
