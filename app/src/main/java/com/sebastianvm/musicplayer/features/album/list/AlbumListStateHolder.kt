package com.sebastianvm.musicplayer.features.album.list

import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.designsystem.components.AlbumRow
import com.sebastianvm.musicplayer.designsystem.components.SortButton
import com.sebastianvm.musicplayer.features.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.album.details.AlbumDetailsUiComponent
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenu
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortMenuUiComponent
import com.sebastianvm.musicplayer.features.sort.SortableListType
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

data class AlbumListState(
    val albums: List<AlbumRow.State> = listOf(),
    val sortButtonState: SortButton.State,
) : State

sealed interface AlbumListUserAction : UserAction {
    data class AlbumMoreIconClicked(val albumId: Long) : AlbumListUserAction

    data object SortButtonClicked : AlbumListUserAction

    data class AlbumClicked(val albumItem: AlbumRow.State) : AlbumListUserAction
}

class AlbumListStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    albumRepository: AlbumRepository,
    sortPreferencesRepository:
        com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository,
    private val navController: NavController,
) : StateHolder<UiState<AlbumListState>, AlbumListUserAction> {

    override val state: StateFlow<UiState<AlbumListState>> =
        combine(
                albumRepository.getAlbums(),
                sortPreferencesRepository.getAlbumListSortPreferences(),
            ) { albums, sortPrefs ->
                if (albums.isEmpty()) {
                    Empty
                } else {
                    Data(
                        AlbumListState(
                            albums = albums.map { album -> AlbumRow.State.fromAlbum(album) },
                            sortButtonState =
                                SortButton.State(
                                    option = sortPrefs.sortOption,
                                    sortOrder = sortPrefs.sortOrder,
                                ),
                        )
                    )
                }
            }
            .stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: AlbumListUserAction) {
        when (action) {
            is AlbumListUserAction.AlbumMoreIconClicked -> {
                navController.push(
                    AlbumContextMenu(
                        arguments = AlbumContextMenuArguments(action.albumId),
                        navController = navController,
                    ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is AlbumListUserAction.SortButtonClicked -> {
                navController.push(
                    SortMenuUiComponent(
                        arguments = SortMenuArguments(listType = SortableListType.Albums)
                    ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is AlbumListUserAction.AlbumClicked -> {
                navController.push(
                    AlbumDetailsUiComponent(
                        arguments =
                            AlbumDetailsArguments(
                                albumId = action.albumItem.id,
                                albumName = action.albumItem.albumName,
                                imageUri = action.albumItem.artworkUri,
                                artists = action.albumItem.artists,
                            ),
                        navController = navController,
                    )
                )
            }
        }
    }
}
