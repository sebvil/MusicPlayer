package com.sebastianvm.musicplayer.ui.library.albumlist

import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class AlbumListStateHolder(
    stateHolderScope: CoroutineScope = stateHolderScope(),
    albumRepository: AlbumRepository,
    sortPreferencesRepository: SortPreferencesRepository
) : StateHolder<UiState<AlbumListState>, AlbumListUserAction> {

    override val state: StateFlow<UiState<AlbumListState>> = combine(
        albumRepository.getAlbums(),
        sortPreferencesRepository.getAlbumListSortPreferences()
    ) { albums, sortPrefs ->
        if (albums.isEmpty()) {
            Empty
        } else {
            Data(
                AlbumListState(
                    modelListState = ModelListState(
                        items = albums.map { album ->
                            album.toModelListItemState()
                        },
                        headerState = HeaderState.None,
                        sortButtonState = SortButtonState(
                            text = sortPrefs.sortOption.stringId,
                            sortOrder = sortPrefs.sortOrder
                        )
                    ),
                )
            )
        }
    }.stateIn(stateHolderScope, SharingStarted.Eagerly, Loading)

    override fun handle(action: AlbumListUserAction) = Unit
}

data class AlbumListState(val modelListState: ModelListState) : State

sealed interface AlbumListUserAction : UserAction
