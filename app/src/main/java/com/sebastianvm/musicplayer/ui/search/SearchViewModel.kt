package com.sebastianvm.musicplayer.ui.search

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.repository.FullTextSearchRepository
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toAlbumRowState
import com.sebastianvm.musicplayer.ui.components.toArtistRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
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
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    initialState: SearchState,
    private val ftsRepository: FullTextSearchRepository
) :
    BaseViewModel<SearchUserAction, SearchUiEvent, SearchState>(initialState) {

    override fun handle(action: SearchUserAction) {
        when (action) {
            is SearchUserAction.OnTextChanged -> {
                collectFirst(ftsRepository.searchTracks(action.newText)) { tracks ->
                    setState {
                        copy(
                            searchTerm = action.newText,
                            trackSearchResults = tracks.map { it.toTrackRowState() },
                        )
                    }
                }

                collectFirst(ftsRepository.searchArtists(action.newText)) { artists ->
                    setState {
                        copy(
                            searchTerm = action.newText,
                            artistSearchResults = artists.map {
                                it.toArtistRowState(
                                    shouldShowContextMenu = true
                                )
                            }
                        )
                    }
                }

                collectFirst(ftsRepository.searchAlbums(action.newText)) { albums ->
                    setState {
                        copy(
                            searchTerm = action.newText,
                            albumSearchResults = albums.map { it.toAlbumRowState() }
                        )
                    }
                }

                collectFirst(ftsRepository.searchGenres(action.newText)) { genres ->
                    setState {
                        copy(
                            searchTerm = action.newText,
                            genreSearchResults = genres
                        )
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
    @StringRes val selectedOption: Int,
    val trackSearchResults: List<TrackRowState>,
    val artistSearchResults: List<ArtistRowState>,
    val albumSearchResults: List<AlbumRowState>,
    val genreSearchResults: List<Genre>,
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialSearchStateModule {
    @Provides
    @ViewModelScoped
    fun initialSearchStateProvider(): SearchState {
        return SearchState(
            searchTerm = "",
            selectedOption = R.string.songs,
            trackSearchResults = listOf(),
            artistSearchResults = listOf(),
            albumSearchResults = listOf(),
            genreSearchResults = listOf(),
        )
    }
}

sealed class SearchUserAction : UserAction {
    data class OnTextChanged(val newText: String) : SearchUserAction()
    data class SearchTypeChanged(@StringRes val newType: Int) : SearchUserAction()
}

sealed class SearchUiEvent : UiEvent

