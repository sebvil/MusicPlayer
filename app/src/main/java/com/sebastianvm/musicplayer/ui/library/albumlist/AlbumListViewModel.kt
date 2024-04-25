package com.sebastianvm.musicplayer.ui.library.albumlist

import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AlbumListViewModel(
    initialState: AlbumListState = AlbumListState(
        ModelListState(
            items = listOf(),
            sortButtonState = null,
            headerState = HeaderState.None
        ),
        isLoading = true
    ),
    viewModelScope: CoroutineScope? = null,
    albumRepository: AlbumRepository,
    sortPreferencesRepository: SortPreferencesRepository
) : BaseViewModel<AlbumListState, AlbumListUserAction>(
    initialState = initialState,
    viewModelScope = viewModelScope
) {

    init {
        albumRepository.getAlbums().onEach { albums ->
            setState {
                it.copy(
                    modelListState = it.modelListState.copy(
                        items = albums.map { album ->
                            album.toModelListItemState()
                        },
                        headerState = HeaderState.None
                    ),
                    isLoading = false
                )
            }
        }.launchIn(vmScope)

        sortPreferencesRepository.getAlbumListSortPreferences()
            .onEach { sortPrefs ->
                setState {
                    it.copy(
                        modelListState = it.modelListState.copy(
                            sortButtonState = SortButtonState(
                                text = sortPrefs.sortOption.stringId,
                                sortOrder = sortPrefs.sortOrder
                            )
                        ),
                    )
                }
            }.launchIn(vmScope)
    }

    override fun handle(action: AlbumListUserAction) = Unit
}

data class AlbumListState(val modelListState: ModelListState, val isLoading: Boolean) : State

sealed interface AlbumListUserAction : UserAction

fun AlbumListState.toUiState(): UiState<AlbumListState> {
    return when {
        isLoading -> Loading
        modelListState.items.isEmpty() -> Empty
        else -> Data(this)
    }
}
