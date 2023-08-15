package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.TrailingButtonType
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.util.coroutines.combineToPair
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistListViewModel @Inject constructor(
    artistRepository: ArtistRepository,
    private val sortPreferencesRepository: SortPreferencesRepository
) : BaseViewModel<ArtistListState, ArtistListUserAction>() {

    init {
        combineToPair(
            artistRepository.getArtists(),
            sortPreferencesRepository.getArtistListSortOrder()
        ).onEach { (artists, sortOrder) ->
            if (artists.isEmpty()) {
                setState { Empty }
            } else {
                setDataState {
                    it.copy(
                        modelListState = ModelListState(
                            items = artists.map { artist ->
                                artist.toModelListItemState(trailingButtonType = TrailingButtonType.More)
                            },
                            sortButtonState = SortButtonState(
                                text = R.string.artist_name,
                                sortOrder = sortOrder
                            ),
                            headerState = HeaderState.None
                        )
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: ArtistListUserAction) {
        when (action) {
            is ArtistListUserAction.SortByButtonClicked -> {
                viewModelScope.launch {
                    sortPreferencesRepository.toggleArtistListSortOrder()
                }
            }
        }
    }

    override val defaultState: ArtistListState by lazy {
        ArtistListState(
            modelListState = ModelListState(
                items = listOf(),
                sortButtonState = SortButtonState(
                    text = R.string.artist_name,
                    sortOrder = MediaSortOrder.ASCENDING
                ),
                headerState = HeaderState.None
            )
        )
    }
}

data class ArtistListState(
    val modelListState: ModelListState
) : State

sealed interface ArtistListUserAction : UserAction {
    data object SortByButtonClicked : ArtistListUserAction
}
