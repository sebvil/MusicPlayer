package com.sebastianvm.musicplayer.features.album.list

import androidx.compose.runtime.Stable
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenu
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.sort.SortMenu
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortableListType
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

@Stable
data class AlbumListState(
    val modelListState: ModelListState,
) : State

sealed interface AlbumListUserAction : UserAction {
    data class AlbumMoreIconClicked(val albumId: Long) : AlbumListUserAction
    data object SortButtonClicked : AlbumListUserAction
    data class AlbumClicked(val albumId: Long) : AlbumListUserAction
}

class AlbumListStateHolder(
    stateHolderScope: CoroutineScope = stateHolderScope(),
    albumRepository: AlbumRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    private val navController: NavController,
) : StateHolder<UiState<AlbumListState>, AlbumListUserAction> {

    override val state: StateFlow<UiState<AlbumListState>> = combine(
        albumRepository.getAlbums(),
        sortPreferencesRepository.getAlbumListSortPreferences(),
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
    }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: AlbumListUserAction) {
        when (action) {
            is AlbumListUserAction.AlbumMoreIconClicked -> {
                navController.push(
                    AlbumContextMenu(
                        arguments = AlbumContextMenuArguments(action.albumId),
                        navController = navController
                    ),
                    navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet)
                )
            }

            is AlbumListUserAction.SortButtonClicked -> {
                navController.push(
                    SortMenu(
                        arguments = SortMenuArguments(listType = SortableListType.Albums)
                    ),
                    navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }

            is AlbumListUserAction.AlbumClicked -> {
                navController.push(
                    TrackList(
                        arguments = TrackListArguments(MediaGroup.Album(action.albumId)),
                        navController = navController
                    )
                )
            }
        }
    }
}

fun getAlbumListStateHolder(
    dependencies: DependencyContainer,
    navController: NavController
): AlbumListStateHolder {
    return AlbumListStateHolder(
        albumRepository = dependencies.repositoryProvider.albumRepository,
        navController = navController,
        sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
    )
}
