package com.sebastianvm.musicplayer.features.artist.list

import com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.ArtistRow
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenu
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistUiComponent
import com.sebastianvm.musicplayer.services.Services
import com.sebastianvm.musicplayer.services.features.mvvm.Data
import com.sebastianvm.musicplayer.services.features.mvvm.Empty
import com.sebastianvm.musicplayer.services.features.mvvm.Loading
import com.sebastianvm.musicplayer.services.features.mvvm.State
import com.sebastianvm.musicplayer.services.features.mvvm.StateHolder
import com.sebastianvm.musicplayer.services.features.mvvm.UiState
import com.sebastianvm.musicplayer.services.features.mvvm.UserAction
import com.sebastianvm.musicplayer.services.features.mvvm.stateHolderScope
import com.sebastianvm.musicplayer.services.features.navigation.NavController
import com.sebastianvm.musicplayer.services.features.navigation.NavOptions
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
                                SortButton.State(
                                    option = SortOptions.Artist, sortOrder = sortOrder),
                        ))
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
                    ArtistContextMenu(
                        arguments = ArtistContextMenuArguments(artistId = action.artistId)),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is ArtistListUserAction.ArtistClicked -> {
                navController.push(
                    ArtistUiComponent(
                        arguments = ArtistArguments(action.artistId),
                        navController = navController,
                    ))
            }
        }
    }
}

fun getArtistListStateHolder(
    services: Services,
    navController: NavController,
): ArtistListStateHolder {
    return ArtistListStateHolder(
        artistRepository = services.repositoryProvider.artistRepository,
        navController = navController,
        sortPreferencesRepository = services.repositoryProvider.sortPreferencesRepository,
    )
}
