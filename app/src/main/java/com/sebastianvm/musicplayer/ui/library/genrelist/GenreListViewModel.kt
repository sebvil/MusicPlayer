package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
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
) : BaseViewModel<GenreListState, GenreListUserAction, GenreListUiEvent>(initialState) {

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
                                trackListType = TrackListType.GENRE,
                                trackListId = action.genreId
                            )
                        )
                    )
                )
            }

            is GenreListUserAction.SortByButtonClicked -> {
                viewModelScope.launch {
                    preferencesRepository.toggleGenreListSortOrder()
                }
            }

            is GenreListUserAction.UpButtonClicked -> addNavEvent(NavEvent.NavigateUp)

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
    object SortByButtonClicked : GenreListUserAction
}

