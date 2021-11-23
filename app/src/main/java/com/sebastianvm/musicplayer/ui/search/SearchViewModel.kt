package com.sebastianvm.musicplayer.ui.search

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
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
                        Log.i("SEARCH", "${action.newText}: $it")
                    }
                }
            }

        }
    }
}

data class SearchState(
    val searchTerm: String,
    val searchResults: List<String>
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialSearchStateModule {
    @Provides
    @ViewModelScoped
    fun initialSearchStateProvider(savedStateHandle: SavedStateHandle): SearchState {
        return SearchState(
            searchTerm = "",
            searchResults = listOf()
        )
    }
}

sealed class SearchUserAction : UserAction {
    data class OnTextChanged(val newText: String) : SearchUserAction()
}
sealed class SearchUiEvent : UiEvent

