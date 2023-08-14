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
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.util.coroutines.combineToPair
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreListViewModel @Inject constructor(
    genreRepository: GenreRepository,
    private val preferencesRepository: SortPreferencesRepository,
) : BaseViewModel<GenreListState, GenreListUserAction>() {

    init {
        combineToPair(
            genreRepository.getGenres(),
            preferencesRepository.getGenreListSortOrder()
        ).onEach { (genreList, sortOrder) ->
            if (genreList.isEmpty()) {
                setState { Empty }
            } else {
                setDataState {
                    it.copy(
                        modelListState = ModelListState(
                            items = genreList.map { genre -> genre.toModelListItemState() },
                            sortButtonState = SortButtonState(
                                text = R.string.genre_name,
                                sortOrder = sortOrder
                            ),
                            headerState = HeaderState.None
                        )
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: GenreListUserAction) {
        when (action) {
            is GenreListUserAction.SortByButtonClicked -> {
                viewModelScope.launch {
                    preferencesRepository.toggleGenreListSortOrder()
                }
            }
        }
    }

    override val defaultState: GenreListState by lazy {
        GenreListState(
            modelListState = ModelListState(
                items = listOf(),
                sortButtonState = null,
                headerState = HeaderState.None
            )
        )
    }

}

data class GenreListState(val modelListState: ModelListState) : State

sealed interface GenreListUserAction : UserAction {
    data object SortByButtonClicked : GenreListUserAction
}

