package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.library.tracks.TrackListArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
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
) : BaseViewModel<GenreListUiEvent, GenreListState>(initialState),
    ViewModelInterface<GenreListState, GenreListUserAction> {

    init {
        preferencesRepository.getGenreListSortOrder().flatMapLatest {
            genreRepository.getGenres(sortOrder = it)
        }.onEach { genreList ->
            setState {
                copy(
                    genreList = genreList.map { it.toModelListItemState() },
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: GenreListUserAction) {
        when (action) {
            is GenreListUserAction.GenreOverflowMenuIconClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.GenreContextMenu(
                            arguments = GenreContextMenuArguments(
                                genreId = action.genreId
                            )
                        )
                    )
                )
            }
            is GenreListUserAction.GenreRowClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackList(
                            TrackListArguments(
                                trackListId = action.genreId,
                                trackListType = TrackListType.GENRE
                            )
                        )
                    )
                )
            }

            is GenreListUserAction.SortByClicked -> {
                viewModelScope.launch {
                    preferencesRepository.modifyGenreListSortOrder()
                }
            }
            GenreListUserAction.UpButtonClicked -> addNavEvent(NavEvent.NavigateUp)

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

sealed interface GenreListUiEvent : UiEvent
sealed interface GenreListUserAction : UserAction {
    data class GenreRowClicked(val genreId: Long) : GenreListUserAction
    data class GenreOverflowMenuIconClicked(val genreId: Long) : GenreListUserAction
    object UpButtonClicked : GenreListUserAction
    object SortByClicked : GenreListUserAction
}

