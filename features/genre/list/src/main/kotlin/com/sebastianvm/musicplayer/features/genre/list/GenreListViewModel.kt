package com.sebastianvm.musicplayer.features.genre.list

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.genre.GenreRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.GenreRow
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.resources.RString
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
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsArguments
import com.sebastianvm.musicplayer.features.api.genre.details.genreDetails
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuArguments
import com.sebastianvm.musicplayer.features.api.genre.menu.genreContextMenu
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GenreListState(val genres: List<GenreRow.State>, val sortButtonState: SortButton.State) :
    State

sealed interface GenreListUserAction : UserAction {
    data object SortByButtonClicked : GenreListUserAction

    data class GenreClicked(val genreId: Long, val genreName: String) : GenreListUserAction

    data class GenreMoreIconClicked(val genreId: Long) : GenreListUserAction
}

class GenreListViewModel(
    genreRepository: GenreRepository,
    private val navController: NavController,
    private val sortPreferencesRepository: SortPreferencesRepository,
    vmScope: CoroutineScope = getViewModelScope(),
    private val features: FeatureRegistry,
) : BaseViewModel<UiState<GenreListState>, GenreListUserAction>(viewModelScope = vmScope) {

    override val state: StateFlow<UiState<GenreListState>> =
        combine(
            genreRepository.getGenres(),
            sortPreferencesRepository.getGenreListSortOrder()
        ) { genres,
            sortOrder ->
            if (genres.isEmpty()) {
                Empty
            } else {
                Data(
                    GenreListState(
                        genres = genres.map { genre -> GenreRow.State.fromGenre(genre) },
                        sortButtonState =
                        SortButton.State(text = RString.genre_name, sortOrder = sortOrder),
                    )
                )
            }
        }
            .stateIn(viewModelScope, SharingStarted.Lazily, Loading)

    override fun handle(action: GenreListUserAction) {
        when (action) {
            is GenreListUserAction.SortByButtonClicked -> {
                viewModelScope.launch { sortPreferencesRepository.toggleGenreListSortOrder() }
            }

            is GenreListUserAction.GenreMoreIconClicked -> {
                navController.push(
                    features
                        .genreContextMenu()
                        .genreContextMenuUiComponent(
                            arguments = GenreContextMenuArguments(action.genreId)
                        ),
                    navOptions =
                    NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }

            is GenreListUserAction.GenreClicked -> {
                navController.push(
                    features
                        .genreDetails()
                        .genreDetailsUiComponent(
                            arguments =
                            GenreDetailsArguments(
                                genreId = action.genreId,
                                genreName = action.genreName,
                            ),
                            navController = navController,
                        )
                )
            }
        }
    }
}
