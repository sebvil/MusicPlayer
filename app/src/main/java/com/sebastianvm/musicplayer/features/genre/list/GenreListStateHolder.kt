package com.sebastianvm.musicplayer.features.genre.list

import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenu
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenuArguments
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.track.list.TrackList
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GenreListState(
    val modelListState: ModelListState,
) : State

sealed interface GenreListUserAction : UserAction {
    data object SortByButtonClicked : GenreListUserAction
    data class GenreClicked(val genreId: Long) : GenreListUserAction
    data class GenreMoreIconClicked(val genreId: Long) : GenreListUserAction
}

class GenreListStateHolder(
    genreRepository: GenreRepository,
    private val navController: NavController,
    private val sortPreferencesRepository: SortPreferencesRepository,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<UiState<GenreListState>, GenreListUserAction> {

    override val state: StateFlow<UiState<GenreListState>> = combine(
        genreRepository.getGenres(),
        sortPreferencesRepository.getGenreListSortOrder(),
    ) { genres, sortOrder ->
        if (genres.isEmpty()) {
            Empty
        } else {
            Data(
                GenreListState(
                    modelListState = ModelListState(
                        items = genres.map { genre ->
                            genre.toModelListItemState()
                        },
                        sortButtonState = SortButtonState(
                            text = R.string.genre_name,
                            sortOrder = sortOrder
                        )
                    ),
                )
            )
        }
    }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: GenreListUserAction) {
        when (action) {
            is GenreListUserAction.SortByButtonClicked -> {
                stateHolderScope.launch {
                    sortPreferencesRepository.toggleGenreListSortOrder()
                }
            }

            is GenreListUserAction.GenreMoreIconClicked -> {
                navController.push(
                    GenreContextMenu(
                        arguments = GenreContextMenuArguments(action.genreId),
                        navController = navController
                    ),
                    navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet)
                )
            }

            is GenreListUserAction.GenreClicked -> {
                navController.push(
                    TrackList(
                        arguments = TrackListArguments(trackListType = MediaGroup.Genre(action.genreId)),
                        navController = navController
                    )
                )
            }
        }
    }
}

fun getGenreListStateHolder(
    dependencies: DependencyContainer,
    navController: NavController
): GenreListStateHolder {
    return GenreListStateHolder(
        genreRepository = dependencies.repositoryProvider.genreRepository,
        navController = navController,
        sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
    )
}
