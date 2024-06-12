package com.sebastianvm.musicplayer.features.artist.list

import androidx.compose.runtime.collectAsState
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.sebastianvm.musicplayer.designsystem.components.ArtistRow
import com.sebastianvm.musicplayer.designsystem.components.SortButton
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenu
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import com.sebastianvm.musicplayer.util.extensions.collectValue
import com.sebastianvm.musicplayer.util.resources.RString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
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
    recompositionMode: RecompositionMode = RecompositionMode.ContextClock,
) : StateHolder<UiState<ArtistListState>, ArtistListUserAction> {
    override val state: StateFlow<UiState<ArtistListState>> =
        stateHolderScope.launchMolecule(recompositionMode) {
            val artists = artistRepository.getArtists().collectValue(initial = null)
            val sortOrder =
                sortPreferencesRepository
                    .getArtistListSortOrder()
                    .collectAsState(initial = null)
                    .value
            when {
                artists == null || sortOrder == null -> Loading
                artists.isEmpty() -> Empty
                else -> {
                    Data(
                        ArtistListState(
                            artists = artists.map { artist -> ArtistRow.State.fromArtist(artist) },
                            sortButtonState =
                                SortButton.State(text = RString.artist_name, sortOrder = sortOrder),
                        )
                    )
                }
            }
        }

    override fun handle(action: ArtistListUserAction) {
        when (action) {
            is ArtistListUserAction.SortByButtonClicked -> {
                stateHolderScope.launch { sortPreferencesRepository.toggleArtistListSortOrder() }
            }
            is ArtistListUserAction.ArtistMoreIconClicked -> {
                navController.push(
                    ArtistContextMenu(
                        arguments = ArtistContextMenuArguments(artistId = action.artistId)
                    ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is ArtistListUserAction.ArtistClicked -> {
                navController.push(
                    ArtistUiComponent(
                        arguments = ArtistArguments(action.artistId),
                        navController = navController,
                    )
                )
            }
        }
    }
}

fun getArtistListStateHolder(
    dependencies: Dependencies,
    navController: NavController,
): ArtistListStateHolder {
    return ArtistListStateHolder(
        artistRepository = dependencies.repositoryProvider.artistRepository,
        navController = navController,
        sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
    )
}
