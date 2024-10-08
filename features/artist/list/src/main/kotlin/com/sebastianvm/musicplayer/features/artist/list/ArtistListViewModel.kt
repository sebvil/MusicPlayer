package com.sebastianvm.musicplayer.features.artist.list

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.ArtistRow
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.Data
import com.sebastianvm.musicplayer.core.ui.mvvm.Empty
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
import com.sebastianvm.musicplayer.features.api.artist.list.ArtistListProps
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.artist.menu.artistContextMenu
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.registry.create
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
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

class ArtistListViewModel(
    artistRepository: ArtistRepository,
    private val props: StateFlow<ArtistListProps>,
    private val sortPreferencesRepository: SortPreferencesRepository,
    viewModelScope: CoroutineScope = getViewModelScope(),
    private val features: FeatureRegistry,
) : BaseViewModel<UiState<ArtistListState>, ArtistListUserAction>(viewModelScope = viewModelScope) {

    private val navController: NavController
        get() = props.value.navController

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
            .stateIn(viewModelScope, SharingStarted.Lazily, Loading)

    override fun handle(action: ArtistListUserAction) {
        when (action) {
            is ArtistListUserAction.SortByButtonClicked -> {
                viewModelScope.launch { sortPreferencesRepository.toggleArtistListSortOrder() }
            }
            is ArtistListUserAction.ArtistMoreIconClicked -> {
                navController.push(
                    features
                        .artistContextMenu()
                        .create(arguments = ArtistContextMenuArguments(artistId = action.artistId)),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is ArtistListUserAction.ArtistClicked -> {
                navController.push(
                    features
                        .artistDetails()
                        .create(
                            arguments = ArtistDetailsArguments(action.artistId),
                            props =
                                MutableStateFlow(ArtistDetailsProps(navController = navController)),
                        )
                )
            }
        }
    }
}
