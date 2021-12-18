package com.sebastianvm.musicplayer.ui.library.albums

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.repository.AlbumRepository
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.ArtLoader
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
class AlbumsListViewModel @Inject constructor(
    initialState: AlbumsListState,
    albumRepository: AlbumRepository,
) : BaseViewModel<AlbumsListUserAction, AlbumsListUiEvent, AlbumsListState>(initialState) {

    init {
        viewModelScope.launch {
            albumRepository.getAlbums().collect { albums ->
                setState {
                    copy(
                        albumsList = albums.map { album ->
                            album.toAlbumsListItem()
                        }.sortedBy { it.albumRowState.albumName },
                    )
                }
            }
        }
    }

    private fun FullAlbumInfo.toAlbumsListItem(): AlbumsListItem {
        return AlbumsListItem(
            albumGid = album.albumGid,
            AlbumRowState(
                albumName = album.albumName,
                image = ArtLoader.getAlbumArt(
                    albumGid = album.albumGid.toLong(),
                    albumName = album.albumName
                ),
                year = album.year,
                artists = artists.joinToString(", ") { it.artistName }
            )
        )
    }

    override fun handle(action: AlbumsListUserAction) {
        when (action) {
            is AlbumsListUserAction.AlbumClicked -> {
                addUiEvent(
                    AlbumsListUiEvent.NavigateToAlbum(
                        action.albumGid,
                        action.albumName
                    )
                )
            }
        }
    }
}

data class AlbumsListState(
    val albumsList: List<AlbumsListItem>
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumsListStateModule {
    @Provides
    @ViewModelScoped
    fun initialAlbumsStateProvider(): AlbumsListState {
        return AlbumsListState(
            albumsList = listOf()
        )
    }
}

sealed class AlbumsListUserAction : UserAction {
    data class AlbumClicked(val albumGid: String, val albumName: String) : AlbumsListUserAction()
}

sealed class AlbumsListUiEvent : UiEvent {
    data class NavigateToAlbum(val albumGid: String, val albumName: String) : AlbumsListUiEvent()
}
