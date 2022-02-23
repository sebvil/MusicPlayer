package com.sebastianvm.musicplayer.ui.library.genres

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.getStringComparator
import com.sebastianvm.musicplayer.util.sort.not
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenresListViewModel @Inject constructor(
    initialState: GenresListState,
    genreRepository: GenreRepository,
    private val preferencesRepository: PreferencesRepository,
) :
    BaseViewModel<GenresListUiEvent, GenresListState>(initialState) {

    init {
        collect(
            preferencesRepository.getGenresListSortOrder()
                .combine(genreRepository.getGenres()) { sortOrder, genresList ->
                    Pair(sortOrder, genresList)
                }) { pair ->
            setState {
                copy(
                    sortOrder = pair.first,
                    genresList = pair.second.sortedWith(getStringComparator(pair.first) { item -> item.genreName }),
                )
            }
        }
    }

    fun onGenreClicked(genreName: String) {
        addUiEvent(GenresListUiEvent.NavigateToGenre(genreName))
    }

    fun onSortByClicked() {
        viewModelScope.launch {
            preferencesRepository.modifyGenresListSortOrder(!state.value.sortOrder)
        }
    }

    fun onUpButtonClicked() {
        addUiEvent(GenresListUiEvent.NavigateUp)
    }

    fun onGenreOverflowMenuIconClicked(genreName: String) {
        addUiEvent(GenresListUiEvent.OpenContextMenu(genreName))
    }
}

data class GenresListState(
    val genresList: List<Genre>,
    val sortOrder: MediaSortOrder,
    override val events: List<GenresListUiEvent>
) : State<GenresListUiEvent> {

    @Suppress("UNCHECKED_CAST")
    override fun <S : State<GenresListUiEvent>> setEvent(events: List<GenresListUiEvent>): S {
        return copy(events = events) as S
    }
}


@InstallIn(ViewModelComponent::class)
@Module
object InitialGenresListStateModule {

    @Provides
    @ViewModelScoped
    fun initialGenresListStateProvider() =
        GenresListState(genresList = listOf(), sortOrder = MediaSortOrder.ASCENDING, events = listOf())
}

sealed class GenresListUiEvent : UiEvent {
    data class NavigateToGenre(val genreName: String) : GenresListUiEvent()
    object NavigateUp : GenresListUiEvent()
    data class OpenContextMenu(val genreName: String) : GenresListUiEvent()
}
