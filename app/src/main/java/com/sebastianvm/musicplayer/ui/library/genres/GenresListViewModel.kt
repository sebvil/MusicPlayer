package com.sebastianvm.musicplayer.ui.library.genres

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.GenreRepository
import com.sebastianvm.musicplayer.repository.PreferencesRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.getStringComparator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenresListViewModel @Inject constructor(
    initialState: GenresListState,
    genreRepository: GenreRepository,
    private val preferencesRepository: PreferencesRepository,
) :
    BaseViewModel<GenresListUserAction, GenresListUiEvent, GenresListState>(initialState) {

    init {
        collect(preferencesRepository.getGenresListSortOrder()) { savedSortOrder ->
            setState {
                copy(
                    sortOrder = savedSortOrder,
                    genresList = genresList.sortedWith(getStringComparator(savedSortOrder) { item -> item.genreName }),
                )
            }
        }
        collect(genreRepository.getGenres()) { genres ->
            setState {
                copy(
                    genresList = genres.map { GenresListItem(it.genreName) }
                        .sortedWith(getStringComparator(state.value.sortOrder) { item -> item.genreName })
                )
            }
        }
    }

    override fun handle(action: GenresListUserAction) {
        when (action) {
            is GenresListUserAction.GenreClicked -> {
                this.addUiEvent(GenresListUiEvent.NavigateToGenre(genreName = action.genreName))
            }
            GenresListUserAction.SortByClicked -> {
                viewModelScope.launch {
                    preferencesRepository.modifyGenresListSortOrder(!state.value.sortOrder)
                }
            }
            GenresListUserAction.UpButtonClicked -> addUiEvent(GenresListUiEvent.NavigateUp)
        }
    }
}

data class GenresListState(
    val genresList: List<GenresListItem>,
    val sortOrder: SortOrder
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialGenresListStateModule {

    @Provides
    @ViewModelScoped
    fun initialGenresListStateProvider() =
        GenresListState(genresList = listOf(), sortOrder = SortOrder.ASCENDING)
}

sealed class GenresListUserAction : UserAction {
    data class GenreClicked(val genreName: String) : GenresListUserAction()
    object UpButtonClicked : GenresListUserAction()
    object SortByClicked : GenresListUserAction()
}

sealed class GenresListUiEvent : UiEvent {
    data class NavigateToGenre(val genreName: String) : GenresListUiEvent()
    object NavigateUp : GenresListUiEvent()
}