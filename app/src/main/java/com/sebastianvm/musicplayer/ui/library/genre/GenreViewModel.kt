package com.sebastianvm.musicplayer.ui.library.genre

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.extensions.getArgs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GenreViewModel @Inject constructor(
    initialState: GenreState,
    genreRepository: GenreRepository,
) : BaseViewModel<GenreUiEvent, GenreState>(initialState),
    ViewModelInterface<GenreState, GenreUserAction> {

    init {
        viewModelScope.launch {
            val genreName = genreRepository.getGenreName(state.value.genreId).first()
            setState { copy(genreName = genreName) }
        }

    }

    override fun handle(action: GenreUserAction) {
        when (action) {
            is GenreUserAction.UpButtonClicked -> addNavEvent(NavEvent.NavigateUp)
            is GenreUserAction.SortByButtonClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.SortMenu(
                            SortMenuArguments(
                                listType = SortableListType.Tracks(TrackListType.GENRE),
                                mediaId = state.value.genreId
                            )
                        )
                    )
                )
            }
        }
    }
}

data class GenreState(val genreId: Long, val genreName: String = "") : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialGenreStateModule {

    @Provides
    @ViewModelScoped
    fun initialGenreStateProvider(savedStateHandle: SavedStateHandle): GenreState {
        val args = savedStateHandle.getArgs<GenreArguments>()
        return GenreState(genreId = args.genreId)
    }
}

sealed class GenreUiEvent : UiEvent

sealed interface GenreUserAction : UserAction {
    object UpButtonClicked : GenreUserAction
    object SortByButtonClicked : GenreUserAction
}
