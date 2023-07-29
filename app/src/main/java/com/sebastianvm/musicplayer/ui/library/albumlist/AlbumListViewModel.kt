package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.DeprecatedBaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    initialState: AlbumListState,
    albumRepository: AlbumRepository,
) : DeprecatedBaseViewModel<AlbumListState, AlbumListUserAction>(initialState) {
    init {
        albumRepository.getAlbums().onEach { albums ->
            setState {
                copy(
                    albumList = albums.map { album ->
                        album.toModelListItemState()
                    }
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: AlbumListUserAction) = Unit
}


data class AlbumListState(val albumList: List<ModelListItemState>) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumListStateModule {
    @Provides
    @ViewModelScoped
    fun initialAlbumsStateProvider(): AlbumListState {
        return AlbumListState(albumList = listOf())
    }
}

sealed interface AlbumListUserAction : UserAction
