package com.sebastianvm.musicplayer.ui.library.genres

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.GenreRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenresListViewModel @Inject constructor(
    initialState: GenresListState,
    genreRepository: GenreRepository
) :
    BaseViewModel<GenresListUserAction, GenresListUiEvent, GenresListState>(initialState) {

    init {
        viewModelScope.launch {
            genreRepository.getGenres().collect { genres ->
                setState {
                    copy(
                        genresList = genres.map { GenresListItem(it.genreName) }
                            .sortedBy { it.genreName }
                    )
                }
            }
        }
    }

    override fun handle(action: GenresListUserAction) {
        when (action) {
            is GenresListUserAction.GenreClicked -> {
                this.addUiEvent(GenresListUiEvent.NavigateToGenre(genreName = action.genreName))
            }
        }
    }
}

data class GenresListState(
    val genresList: List<GenresListItem>
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialGenresListStateModule {

    @Provides
    @ViewModelScoped
    fun initialGenresListStateProvider() = GenresListState(genresList = listOf())
}

sealed class GenresListUserAction : UserAction {
    data class GenreClicked(val genreName: String) : GenresListUserAction()
}

sealed class GenresListUiEvent : UiEvent {
    data class NavigateToGenre(val genreName: String) : GenresListUiEvent()
}