package com.sebastianvm.musicplayer.features.artist.list

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenu
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistScreen
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.TrailingButtonType
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.rememberStateHolder
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ArtistListState(
    val modelListState: ModelListState,
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
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<UiState<ArtistListState>, ArtistListUserAction> {
    override val state: StateFlow<UiState<ArtistListState>> = combine(
        artistRepository.getArtists(),
        sortPreferencesRepository.getArtistListSortOrder(),
    ) { artists, sortOrder ->
        if (artists.isEmpty()) {
            Empty
        } else {
            Data(
                ArtistListState(
                    modelListState = ModelListState(
                        items = artists.map { artist ->
                            artist.toModelListItemState(trailingButtonType = TrailingButtonType.More)
                        },
                        sortButtonState = SortButtonState(
                            text = R.string.artist_name,
                            sortOrder = sortOrder
                        ),
                        headerState = HeaderState.None
                    ),
                )
            )
        }
    }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: ArtistListUserAction) {
        when (action) {
            is ArtistListUserAction.SortByButtonClicked -> {
                stateHolderScope.launch {
                    sortPreferencesRepository.toggleArtistListSortOrder()
                }
            }

            is ArtistListUserAction.ArtistMoreIconClicked -> {
                navController.push(
                    ArtistContextMenu(
                        arguments = ArtistContextMenuArguments(artistId = action.artistId),
                        navController = navController
                    ),
                    navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet)
                )
            }

            is ArtistListUserAction.ArtistClicked -> {
                navController.push(
                    ArtistScreen(
                        arguments = ArtistArguments(action.artistId),
                        navController = navController
                    )
                )
            }
        }
    }
}

@Composable
fun rememberArtistListStateHolder(navController: NavController): ArtistListStateHolder {
    return rememberStateHolder { dependencies ->
        ArtistListStateHolder(
            artistRepository = dependencies.repositoryProvider.artistRepository,
            navController = navController,
            sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
        )
    }
}
