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
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ArtistListViewModel(
    initialState: ArtistListState = ArtistListState(
        modelListState = ModelListState(
            items = listOf(),
            sortButtonState = SortButtonState(
                text = R.string.artist_name,
                sortOrder = MediaSortOrder.ASCENDING
            ),
            headerState = HeaderState.None
        ),
        isLoading = true
    ),
    viewModelScope: CoroutineScope? = null,
    artistRepository: ArtistRepository,
    private val sortPreferencesRepository: SortPreferencesRepository
) : BaseViewModel<ArtistListState, ArtistListUserAction>(
    initialState = initialState,
    viewModelScope = viewModelScope
) {

    init {
        artistRepository.getArtists().onEach { artists ->
            setState {
                it.copy(
                    modelListState = it.modelListState.copy(
                        items = artists.map { artist ->
                            artist.toModelListItemState(trailingButtonType = TrailingButtonType.More)
                        },
                    ),
                    isLoading = false
                )
            }
        }.launchIn(vmScope)
        sortPreferencesRepository.getArtistListSortOrder().onEach { sortOrder ->
            setState {
                it.copy(
                    modelListState = it.modelListState.copy(
                        sortButtonState = SortButtonState(
                            text = R.string.artist_name,
                            sortOrder = sortOrder
                        ),
                    )
                )
            }
        }.launchIn(vmScope)
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
}

data class ArtistListState(
    val modelListState: ModelListState,
    val isLoading: Boolean
) : State

sealed interface ArtistListUserAction : UserAction {
    data object SortByButtonClicked : ArtistListUserAction
}

fun ArtistListState.toUiState(): UiState<ArtistListState> {
    return when {
        isLoading -> Loading
        modelListState.items.isEmpty() -> Empty
        else -> Data(this)
    }
}
