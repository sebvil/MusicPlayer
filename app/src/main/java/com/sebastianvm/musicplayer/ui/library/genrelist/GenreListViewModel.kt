package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreListViewModel @Inject constructor(
    initialState: GenreListState,
    genreRepository: GenreRepository,
    private val preferencesRepository: SortPreferencesRepository,
) : BaseViewModel<GenreListState, GenreListUserAction, UiEvent>(initialState) {

    init {
        genreRepository.getGenres().onEach { genreList ->
            setState {
                copy(
                    genreList = genreList.map { it.toModelListItemState() },
                )
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

}

data class GenreListState(val genreList: List<ModelListItemState>) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialGenreListStateModule {

    @Provides
    @ViewModelScoped
    fun initialGenreListStateProvider() =
        GenreListState(genreList = listOf())
}

sealed interface GenreListUserAction : UserAction {
    object SortByButtonClicked : GenreListUserAction
}

