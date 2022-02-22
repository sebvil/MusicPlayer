package com.sebastianvm.musicplayer.ui.library.albums

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.ui.components.toAlbumRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.getLongComparator
import com.sebastianvm.musicplayer.util.sort.getStringComparator
import com.sebastianvm.musicplayer.util.sort.id
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.combine
import javax.inject.Inject


@HiltViewModel
class AlbumsListViewModel @Inject constructor(
    initialState: AlbumsListState,
    albumRepository: AlbumRepository,
    preferencesRepository: PreferencesRepository,
) : BaseViewModel<AlbumsListUiEvent, AlbumsListState>(initialState) {

    init {
        collect(
            preferencesRepository.getAlbumsListSortOptions()
                .combine(albumRepository.getAlbums()) { sortSettings, albums ->
                    Pair(sortSettings, albums)
                }) { (sortSettings, albums) ->
            setState {
                copy(
                    currentSort = sortSettings.sortOption,
                    albumsList = albums.map { album ->
                        album.toAlbumRowState()
                    }.sortedWith(getComparator(sortSettings.sortOrder, sortSettings.sortOption)),
                    sortOrder = sortSettings.sortOrder
                )
            }
        }
    }

    fun onAlbumClicked(albumId: String) {
        addUiEvent(AlbumsListUiEvent.NavigateToAlbum(albumId))
    }

    fun onUpButtonClicked() {
        addUiEvent(AlbumsListUiEvent.NavigateUp)
    }

    fun onSortByClicked() {
        addUiEvent(
            AlbumsListUiEvent.ShowSortBottomSheet(
                sortOption = state.value.currentSort.id,
                sortOrder = state.value.sortOrder
            )
        )
    }

    fun onAlbumOverflowMenuIconClicked(albumId: String) {
        addUiEvent(AlbumsListUiEvent.OpenContextMenu(albumId))
    }

    private fun getComparator(
        sortOrder: MediaSortOrder,
        sortOption: MediaSortOption
    ): Comparator<AlbumRowState> {
        return when (sortOption) {
            MediaSortOption.ARTIST -> getStringComparator(sortOrder) { albumRowState -> albumRowState.artists }
            MediaSortOption.ALBUM -> getStringComparator(sortOrder) { albumRowState -> albumRowState.albumName }
            MediaSortOption.YEAR -> getLongComparator(sortOrder) { albumRowState -> albumRowState.year }
            else -> throw throw IllegalStateException("Invalid sort option for Albums list: $sortOption")
        }
    }
}

data class AlbumsListState(
    val albumsList: List<AlbumRowState>,
    val currentSort: MediaSortOption,
    val sortOrder: MediaSortOrder,
    override val events: AlbumsListUiEvent?
) : State<AlbumsListUiEvent> {

    @Suppress("UNCHECKED_CAST")
    override fun <S : State<AlbumsListUiEvent>> setEvent(event: AlbumsListUiEvent?): S {
        return copy(events = event) as S
    }
}

@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumsListStateModule {
    @Provides
    @ViewModelScoped
    fun initialAlbumsStateProvider(): AlbumsListState {
        return AlbumsListState(
            albumsList = listOf(),
            currentSort = MediaSortOption.ALBUM,
            sortOrder = MediaSortOrder.ASCENDING,
            events = null
        )
    }
}

sealed class AlbumsListUiEvent : UiEvent {
    data class NavigateToAlbum(val albumId: String) : AlbumsListUiEvent()
    object NavigateUp : AlbumsListUiEvent()
    data class ShowSortBottomSheet(@StringRes val sortOption: Int, val sortOrder: MediaSortOrder) :
        AlbumsListUiEvent()

    object ScrollToTop : AlbumsListUiEvent()

    data class OpenContextMenu(val albumId: String) : AlbumsListUiEvent()
}
