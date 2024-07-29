package com.sebastianvm.musicplayer.features.artist.list

import com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.ArtistRow
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.core.ui.mvvm.Data
import com.sebastianvm.musicplayer.core.ui.mvvm.Empty
import com.sebastianvm.musicplayer.core.ui.mvvm.Loading
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.core.ui.mvvm.UiState
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.stateHolderScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.api.artist.details.artistDetails
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.artist.menu.artistContextMenu
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ArtistListState(
    val artists: List<ArtistRow.State>,
    val sortButtonState: SortButton.State,
) : State

sealed interface ArtistListUserAction : UserAction {
    data object SortByButtonClicked : ArtistListUserAction

    data class ArtistMoreIconClicked(val artistId: Long) : ArtistListUserAction

    data class ArtistClicked(val artistId: Long) : ArtistListUserAction
}

class ArtistListStateHolder(
    artistRepository: ArtistRepository,
    private val navController: NavController,
    private val sortPreferencesRepository: SortPreferencesRepository,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    private val features: FeatureRegistry,
) : StateHolder<UiState<ArtistListState>, ArtistListUserAction> {
    override val state: StateFlow<UiState<ArtistListState>> =
        combine(
                artistRepository.getArtists(),
                sortPreferencesRepository.getArtistListSortOrder(),
            ) { artists, sortOrder ->
                if (artists.isEmpty()) {
                    Empty
                } else {
                    Data(
                        ArtistListState(
                            artists = artists.map { artist -> ArtistRow.State.fromArtist(artist) },
                            sortButtonState =
                                SortButton.State(option = SortOptions.Artist, sortOrder = sortOrder),
                        )
                    )
                }
            }
            .stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: ArtistListUserAction) {
        when (action) {
            is ArtistListUserAction.SortByButtonClicked -> {
                stateHolderScope.launch { sortPreferencesRepository.toggleArtistListSortOrder() }
            }
            is ArtistListUserAction.ArtistMoreIconClicked -> {
                navController.push(
                    features
                        .artistContextMenu()
                        .artistContextMenuUiComponent(
                            arguments = ArtistContextMenuArguments(artistId = action.artistId)
                        ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is ArtistListUserAction.ArtistClicked -> {
                navController.push(
                    features
                        .artistDetails()
                        .artistDetailsUiComponent(
                            arguments = ArtistDetailsArguments(action.artistId),
                            navController = navController,
                        )
                )
            }
        }
    }
}
