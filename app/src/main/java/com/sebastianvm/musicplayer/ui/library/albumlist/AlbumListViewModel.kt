package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.ui.album.AlbumArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
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
) : BaseViewModel<AlbumListUiEvent, AlbumListState>(initialState),
    ViewModelInterface<AlbumListState, AlbumListUserAction> {

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

    override fun handle(action: AlbumListUserAction) {
        when (action) {
            is AlbumListUserAction.AlbumClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.Album(
                            AlbumArguments(action.albumId)
                        )
                    )
                )
            }
            is AlbumListUserAction.AlbumOverflowIconClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.AlbumContextMenu(
                            AlbumContextMenuArguments(action.albumId)
                        )
                    )
                )
            }
            AlbumListUserAction.SortByClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.SortMenu(
                            SortMenuArguments(
                                listType = SortableListType.Albums
                            )
                        )
                    )
                )
            }
            AlbumListUserAction.UpButtonClicked -> addNavEvent(NavEvent.NavigateUp)
        }
    }
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

sealed class AlbumListUiEvent : UiEvent {
    object ScrollToTop : AlbumListUiEvent()
}

sealed interface AlbumListUserAction : UserAction {
    data class AlbumClicked(val albumId: Long) : AlbumListUserAction
    object UpButtonClicked : AlbumListUserAction
    object SortByClicked : AlbumListUserAction
    data class AlbumOverflowIconClicked(val albumId: Long) : AlbumListUserAction
}