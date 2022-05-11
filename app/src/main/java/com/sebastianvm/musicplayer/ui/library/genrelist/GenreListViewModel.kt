package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.not
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GenreListViewModel @Inject constructor(
    initialState: GenreListState,
    genreRepository: GenreRepository,
    private val preferencesRepository: SortPreferencesRepository,
) : BaseViewModel<GenreListUiEvent, GenreListState>(initialState) {

    init {
        preferencesRepository.getGenreListSortOrder().flatMapLatest {
            setState {
                copy(
                    sortOrder = it
                )
            }
            genreRepository.getGenres(sortOrder = it)
        }.onEach { genreList ->
            setState {
                copy(
                    genreList = genreList,
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onGenreClicked(genreId: Long) {
        addUiEvent(GenreListUiEvent.NavigateToGenre(genreId))
    }

    fun onSortByClicked() {
        viewModelScope.launch {
            preferencesRepository.modifyGenreListSortOrder(!state.value.sortOrder)
        }
    }

    fun onUpButtonClicked() {
        addUiEvent(GenreListUiEvent.NavigateUp)
    }

    fun onGenreOverflowMenuIconClicked(genreId: Long) {
        addUiEvent(GenreListUiEvent.OpenContextMenu(genreId))
    }
}

data class GenreListState(
    val genreList: List<Genre>,
    val sortOrder: MediaSortOrder
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialGenreListStateModule {

    @Provides
    @ViewModelScoped
    fun initialGenreListStateProvider() =
        GenreListState(
            genreList = listOf(),
            sortOrder = MediaSortOrder.ASCENDING,
        )
}

sealed class GenreListUiEvent : UiEvent {
    data class NavigateToGenre(val genreId: Long) : GenreListUiEvent()
    object NavigateUp : GenreListUiEvent()
    data class OpenContextMenu(val genreId: Long) : GenreListUiEvent()
}