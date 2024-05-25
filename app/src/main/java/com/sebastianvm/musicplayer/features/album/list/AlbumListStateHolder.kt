package com.sebastianvm.musicplayer.features.album.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuDelegate
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuStateHolder
import com.sebastianvm.musicplayer.features.album.menu.albumContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.Screen
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortMenuStateHolder
import com.sebastianvm.musicplayer.features.sort.SortableListType
import com.sebastianvm.musicplayer.features.sort.sortMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.track.list.TrackList
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Delegate
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.NoDelegate
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.getStateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolder
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

interface AlbumListDelegate : Delegate, NavController

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
    data class AlbumClicked(val albumId: Long) : AlbumListUserAction
}

class AlbumListStateHolder(
    stateHolderScope: CoroutineScope = stateHolderScope(),
    albumRepository: AlbumRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    sortMenuStateHolderFactory: StateHolderFactory<SortMenuArguments, NoDelegate, SortMenuStateHolder>,
    private val delegate: AlbumListDelegate,
    private val albumContextMenuStateHolderFactory:
    StateHolderFactory<AlbumContextMenuArguments, AlbumContextMenuDelegate, AlbumContextMenuStateHolder>
) : StateHolder<UiState<AlbumListState>, AlbumListUserAction> {

    private val _contextMenuAlbumId: MutableStateFlow<Long?> = MutableStateFlow(null)
    private val _showSortMenu: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val sortMenuStateHolder = sortMenuStateHolderFactory.getStateHolder(
        SortMenuArguments(
            SortableListType.Albums
        )
    )

    override val state: StateFlow<UiState<AlbumListState>> = combine(
        albumRepository.getAlbums(),
        sortPreferencesRepository.getAlbumListSortPreferences(),
        _contextMenuAlbumId,
        _showSortMenu
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
                            arguments = AlbumContextMenuArguments(
                                albumId
                            ),
                            delegate = object : AlbumContextMenuDelegate {
                                override fun push(screen: Screen<*>) {
                                    _contextMenuAlbumId.update { null }
                                    delegate.push(screen)
                                }

                                override fun pop() {
                                    _contextMenuAlbumId.update { null }
                                }

                            }
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
                _contextMenuAlbumId.update { action.albumId }
            }

            is AlbumListUserAction.AlbumContextMenuDismissed -> {
                _contextMenuAlbumId.update { null }
            }

            is AlbumListUserAction.SortButtonClicked -> {
                _showSortMenu.update { true }
            }

            is AlbumListUserAction.SortMenuDismissed -> {
                _showSortMenu.update { false }
            }

            is AlbumListUserAction.AlbumClicked -> {
                delegate.push(
                    TrackList(
                        arguments = TrackListArguments(MediaGroup.Album(action.albumId)),
                        navController = delegate
                    )
                )
            }
        }
    }
}

@Composable
fun rememberAlbumListStateHolder(navController: NavController): AlbumListStateHolder {
    val albumContextMenuStateHolderFactory = albumContextMenuStateHolderFactory()
    val sortMenuStateHolderFactory = sortMenuStateHolderFactory()
    return stateHolder { dependencies ->
        AlbumListStateHolder(
            albumRepository = dependencies.repositoryProvider.albumRepository,
            delegate = object : AlbumListDelegate, NavController by navController {},
            sortMenuStateHolderFactory = sortMenuStateHolderFactory,
            sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
            albumContextMenuStateHolderFactory = albumContextMenuStateHolderFactory
        )
    }
}