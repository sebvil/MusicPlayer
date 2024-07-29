package com.sebastianvm.musicplayer.features.genre.list

import com.sebastianvm.musicplayer.core.data.genre.GenreRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.GenreRow
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.resources.RString
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
import com.sebastianvm.musicplayer.features.genre.details.GenreDetailsArguments
import com.sebastianvm.musicplayer.features.genre.details.GenreDetailsUiComponent
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenu
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenuArguments
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

class GenreListStateHolder(
    genreRepository: GenreRepository,
    private val navController: NavController,
    private val sortPreferencesRepository: SortPreferencesRepository,
    override val stateHolderScope: CoroutineScope = stateHolderScope()) : StateHolder<UiState<GenreListState>, GenreListUserAction> {

    override val state: StateFlow<UiState<GenreListState>> =
        combine(genreRepository.getGenres(), sortPreferencesRepository.getGenreListSortOrder()) {
                genres,
                sortOrder ->
                if (genres.isEmpty()) {
                    Empty
                } else {
                    Data(
                        GenreListState(
                            genres = genres.map { genre -> GenreRow.State.fromGenre(genre) },
                            sortButtonState =
                                SortButton.State(text = RString.genre_name, sortOrder = sortOrder),
                        ))
                }
            }
            .stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: GenreListUserAction) {
        when (action) {
            is GenreListUserAction.SortByButtonClicked -> {
                stateHolderScope.launch { sortPreferencesRepository.toggleGenreListSortOrder() }
            }
            is GenreListUserAction.GenreMoreIconClicked -> {
                navController.push(
                    GenreContextMenu(arguments = GenreContextMenuArguments(action.genreId)),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is GenreListUserAction.GenreClicked -> {
                navController.push(
                    GenreDetailsUiComponent(
                        arguments =
                            GenreDetailsArguments(
                                genreId = action.genreId,
                                genreName = action.genreName,
                            ),
                        navController = navController,
                    ))
            }
        }
    }
}
