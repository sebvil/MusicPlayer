package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GenreListViewModel(
    initialState: GenreListState = GenreListState(
        modelListState = ModelListState(
            items = listOf(),
            sortButtonState = SortButtonState(
                text = R.string.genre_name,
                sortOrder = MediaSortOrder.ASCENDING
            ),
            headerState = HeaderState.None
        ),
        isLoading = true
    ),
    viewModelScope: CoroutineScope? = null,
    genreRepository: GenreRepository,
    private val sortPreferencesRepository: SortPreferencesRepository
) : BaseViewModel<GenreListState, GenreListUserAction>(
    initialState = initialState,
    viewModelScope = viewModelScope
) {

    init {
        genreRepository.getGenres().onEach { genres ->
            setState {
                it.copy(
                    modelListState = it.modelListState.copy(
                        items = genres.map { genre ->
                            genre.toModelListItemState()
                        },
                    ),
                    isLoading = false
                )
            }
        }.launchIn(vmScope)
        sortPreferencesRepository.getGenreListSortOrder().onEach { sortOrder ->
            setState {
                it.copy(
                    modelListState = it.modelListState.copy(
                        sortButtonState = SortButtonState(
                            text = R.string.genre_name,
                            sortOrder = sortOrder
                        ),
                    )
                )
            }
        }.launchIn(vmScope)
    }

    override fun handle(action: GenreListUserAction) {
        when (action) {
            is GenreListUserAction.SortByButtonClicked -> {
                viewModelScope.launch {
                    sortPreferencesRepository.toggleGenreListSortOrder()
                }
            }
        }
    }
}

data class GenreListState(
    val modelListState: ModelListState,
    val isLoading: Boolean
) : State

sealed interface GenreListUserAction : UserAction {
    data object SortByButtonClicked : GenreListUserAction
}

fun GenreListState.toUiState(): UiState<GenreListState> {
    return when {
        isLoading -> Loading
        modelListState.items.isEmpty() -> Empty
        else -> Data(this)
    }
}
