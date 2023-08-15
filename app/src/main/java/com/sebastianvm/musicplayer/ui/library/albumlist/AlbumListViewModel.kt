package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    albumRepository: AlbumRepository
) : BaseViewModel<AlbumListState, AlbumListUserAction>() {
    init {
        albumRepository.getAlbums().onEach { albums ->
            setDataState {
                it.copy(
                    modelListState = ModelListState(
                        items = albums.map { album ->
                            album.toModelListItemState()
                        },
                        sortButtonState = null,
                        headerState = HeaderState.None
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: AlbumListUserAction) = Unit

    override val defaultState: AlbumListState by lazy {
        AlbumListState(
            ModelListState(
                items = listOf(),
                sortButtonState = null,
                headerState = HeaderState.None
            )
        )
    }
}

data class AlbumListState(val modelListState: ModelListState) : State

sealed interface AlbumListUserAction : UserAction
