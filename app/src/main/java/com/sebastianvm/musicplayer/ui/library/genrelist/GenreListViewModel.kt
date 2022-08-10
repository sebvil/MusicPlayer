package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuArguments
import com.sebastianvm.musicplayer.ui.library.tracks.TrackListArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
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
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.TrackList(
                    TrackListArguments(
                        trackListId = genreId,
                        trackListType = TrackListType.GENRE
                    )
                )
            )
        )

    }

    fun onSortByClicked() {
        viewModelScope.launch {
            preferencesRepository.modifyGenreListSortOrder(!state.value.sortOrder)
        }
    }

    fun onUpButtonClicked() {
        addNavEvent(NavEvent.NavigateUp)
    }

    fun onGenreOverflowMenuIconClicked(genreId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.GenreContextMenu(
                    arguments = GenreContextMenuArguments(
                        genreId = genreId
                    )
                )
            )
        )
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

sealed class GenreListUiEvent : UiEvent
