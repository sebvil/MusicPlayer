package com.sebastianvm.musicplayer.ui.library.genres

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.getStringComparator
import com.sebastianvm.musicplayer.util.not
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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

    override fun handle(action: GenresListUserAction) {
        when (action) {
            is GenresListUserAction.GenreClicked -> {
                this.addUiEvent(GenresListUiEvent.NavigateToGenre(genreName = action.genreName))
            }
            is GenresListUserAction.SortByClicked -> {
                viewModelScope.launch {
                    preferencesRepository.modifyGenresListSortOrder(!state.value.sortOrder)
                }
            }
            is GenresListUserAction.UpButtonClicked -> addUiEvent(GenresListUiEvent.NavigateUp)
            is GenresListUserAction.OverflowMenuIconClicked -> {
                viewModelScope.launch {
                    val sortSettings =
                        preferencesRepository.getTracksListSortOptions(TracksListType.GENRE, action.genreName).first()
                    // TODO do not pass sort settings to context menu
                    addUiEvent(
                        GenresListUiEvent.OpenContextMenu(
                            action.genreName,
                            sortSettings.sortOption,
                            sortSettings.sortOrder
                        )
                    )
                }
            }
        }
    }
}

data class GenresListState(
    val genresList: List<Genre>,
    val sortOrder: MediaSortOrder
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialGenresListStateModule {

    @Provides
    @ViewModelScoped
    fun initialGenresListStateProvider() =
        GenresListState(genresList = listOf(), sortOrder = MediaSortOrder.ASCENDING)
}

sealed class GenresListUserAction : UserAction {
    data class GenreClicked(val genreName: String) : GenresListUserAction()
    object UpButtonClicked : GenresListUserAction()
    object SortByClicked : GenresListUserAction()
    data class OverflowMenuIconClicked(val genreName: String) : GenresListUserAction()
}

sealed class GenresListUiEvent : UiEvent {
    data class NavigateToGenre(val genreName: String) : GenresListUiEvent()
    object NavigateUp : GenresListUiEvent()
    data class OpenContextMenu(
        val genreName: String,
        val currentSort: MediaSortOption,
        val sortOrder: MediaSortOrder
    ) : GenresListUiEvent()
}
