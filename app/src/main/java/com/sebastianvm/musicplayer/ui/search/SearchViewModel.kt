package com.sebastianvm.musicplayer.ui.search

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.FullTextSearchRepository
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(initialState: SearchState, private val ftsRepository: FullTextSearchRepository) :
    BaseViewModel<SearchUserAction, SearchUiEvent, SearchState>(initialState) {

    override fun handle(action: SearchUserAction) {
        when (action) {
            is SearchUserAction.OnTextChanged -> {
                setState {
                    copy(
                        searchTerm = action.newText,
                    )
                }
                viewModelScope.launch {
                    ftsRepository.searchTracks(action.newText).map { tracks ->
                        tracks.map { it.trackName }
                    }.collect {
                        setState {
                            copy(
                                searchResults = it
                            )
                        }
                    }
                }
            }
            is SearchUserAction.SearchTypeChanged -> {
                setState {
                    copy(
                        selectedOption = action.newType
                    )
                }
            }

        }
    }
}

data class SearchState(
    val searchTerm: String,
    val searchResults: List<String>,
    @StringRes val selectedOption: Int,
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialSearchStateModule {
    @Provides
    @ViewModelScoped
    fun initialSearchStateProvider(): SearchState {
        return SearchState(
            searchTerm = "",
            searchResults = listOf(),
            selectedOption = R.string.songs
        )
    }
}

sealed class SearchUserAction : UserAction {
    data class OnTextChanged(val newText: String) : SearchUserAction()
    data class SearchTypeChanged(@StringRes val newType: Int): SearchUserAction()
}
sealed class SearchUiEvent : UiEvent

