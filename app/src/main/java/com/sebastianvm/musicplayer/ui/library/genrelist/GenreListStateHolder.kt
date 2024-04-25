package com.sebastianvm.musicplayer.ui.library.genrelist

import com.sebastianvm.musicplayer.R
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

class GenreListStateHolder(
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
    genreRepository: GenreRepository,
    private val sortPreferencesRepository: SortPreferencesRepository
) : StateHolder<UiState<GenreListState>, GenreListUserAction> {

    override val state: StateFlow<UiState<GenreListState>> = combine(
        genreRepository.getGenres(),
        sortPreferencesRepository.getGenreListSortOrder()
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
        }
    }
}

data class GenreListState(
    val modelListState: ModelListState,
) : State

sealed interface GenreListUserAction : UserAction {
    data object SortByButtonClicked : GenreListUserAction
}
