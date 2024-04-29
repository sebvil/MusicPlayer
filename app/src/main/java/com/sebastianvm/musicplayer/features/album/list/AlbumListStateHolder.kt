package com.sebastianvm.musicplayer.features.album.list

import androidx.compose.runtime.Stable
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuStateHolder
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortMenuStateHolder
import com.sebastianvm.musicplayer.features.sort.SortMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.sort.SortableListType
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class AlbumListStateHolder(
    stateHolderScope: CoroutineScope = stateHolderScope(),
    albumRepository: AlbumRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    sortMenuStateHolderFactory: SortMenuStateHolderFactory,
    private val albumContextMenuStateHolderFactory: AlbumContextMenuStateHolderFactory
) : StateHolder<UiState<AlbumListState>, AlbumListUserAction> {

    private val contextMenuAlbumId: MutableStateFlow<Long?> = MutableStateFlow(null)
    private val showSortMenu: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val sortMenuStateHolder = sortMenuStateHolderFactory.getStateHolder(
        SortMenuArguments(
            SortableListType.Albums
        )
    )

    override val state: StateFlow<UiState<AlbumListState>> = combine(
        albumRepository.getAlbums(),
        sortPreferencesRepository.getAlbumListSortPreferences(),
        contextMenuAlbumId,
        showSortMenu
    ) { albums, sortPrefs, contextMenuAlbumId, showSortMenu ->
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
                    albumContextMenuStateHolder = contextMenuAlbumId?.let { albumId ->
                        albumContextMenuStateHolderFactory.getStateHolder(
                            AlbumContextMenuArguments(
                                albumId
                            )
                        )
                    },
                    sortMenuStateHolder = if (showSortMenu) {
                        sortMenuStateHolder
                    } else {
                        null
                    }
                )
            )
        }
    }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: AlbumListUserAction) {
        when (action) {
            is AlbumListUserAction.AlbumMoreIconClicked -> {
                contextMenuAlbumId.update { action.albumId }
            }

            is AlbumListUserAction.AlbumContextMenuDismissed -> {
                contextMenuAlbumId.update { null }
            }

            is AlbumListUserAction.SortButtonClicked -> {
                showSortMenu.update { true }
            }

            is AlbumListUserAction.SortMenuDismissed -> {
                showSortMenu.update { false }
            }
        }
    }
}

@Stable
data class AlbumListState(
    val modelListState: ModelListState,
    val albumContextMenuStateHolder: AlbumContextMenuStateHolder? = null,
    val sortMenuStateHolder: SortMenuStateHolder?,
) : State

sealed interface AlbumListUserAction : UserAction {
    data class AlbumMoreIconClicked(val albumId: Long) : AlbumListUserAction
    data object AlbumContextMenuDismissed : AlbumListUserAction
    data object SortButtonClicked : AlbumListUserAction
    data object SortMenuDismissed : AlbumListUserAction
}
