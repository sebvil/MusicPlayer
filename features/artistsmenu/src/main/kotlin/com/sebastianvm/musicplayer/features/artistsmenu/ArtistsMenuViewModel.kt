package com.sebastianvm.musicplayer.features.artistsmenu

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
import com.sebastianvm.musicplayer.core.designsystems.components.ArtistRow
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.Data
import com.sebastianvm.musicplayer.core.ui.mvvm.Loading
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UiState
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsProps
import com.sebastianvm.musicplayer.features.api.artist.details.artistDetails
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuProps
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.registry.create
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ArtistsMenuState(val artists: List<ArtistRow.State>) : State

sealed interface ArtistsMenuUserAction : UserAction {
    data class ArtistClicked(val artistId: Long) : ArtistsMenuUserAction
}

class ArtistsMenuViewModel(
    vmScope: CoroutineScope = getViewModelScope(),
    arguments: ArtistsMenuArguments,
    artistRepository: ArtistRepository,
    private val props: StateFlow<ArtistsMenuProps>,
    private val features: FeatureRegistry,
) : BaseViewModel<UiState<ArtistsMenuState>, ArtistsMenuUserAction>(viewModelScope = vmScope) {

    private val navController: NavController
        get() = props.value.navController

    override val state: StateFlow<UiState<ArtistsMenuState>> =
        artistRepository
            .getArtistsForMedia(arguments.media)
            .map { artists ->
                Data(
                    ArtistsMenuState(
                        artists = artists.map { artist -> ArtistRow.State.fromArtist(artist) }
                    )
                )
            }
            .stateIn(viewModelScope, Lazily, Loading)

    override fun handle(action: ArtistsMenuUserAction) {
        when (action) {
            is ArtistsMenuUserAction.ArtistClicked -> {
                navController.push(
                    features
                        .artistDetails()
                        .create(
                            arguments = ArtistDetailsArguments(artistId = action.artistId),
                            props =
                                MutableStateFlow(ArtistDetailsProps(navController = navController)),
                        ),
                    navOptions = NavOptions(popCurrent = true),
                )
            }
        }
    }
}
