package com.sebastianvm.musicplayer.features.album.list

import androidx.compose.runtime.collectAsState
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.sebastianvm.musicplayer.designsystem.components.AlbumRow
import com.sebastianvm.musicplayer.designsystem.components.SortButton
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenu
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortMenuUiComponent
import com.sebastianvm.musicplayer.features.sort.SortableListType
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.features.track.list.TrackListUiComponent
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

data class AlbumListState(
    val albums: List<AlbumRow.State> = listOf(),
    val sortButtonState: SortButton.State,
) : State

sealed interface AlbumListUserAction : UserAction {
    data class AlbumMoreIconClicked(val albumId: Long) : AlbumListUserAction

    data object SortButtonClicked : AlbumListUserAction

    data class AlbumClicked(val albumId: Long) : AlbumListUserAction
}

class AlbumListStateHolder(
    recompositionMode: RecompositionMode = RecompositionMode.ContextClock,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    albumRepository: AlbumRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    private val navController: NavController,
) : StateHolder<UiState<AlbumListState>, AlbumListUserAction> {

    override val state: StateFlow<UiState<AlbumListState>> =
        stateHolderScope.launchMolecule(recompositionMode) {
            val albums = albumRepository.getAlbums().collectAsState(initial = null).value
            val sortPrefs =
                sortPreferencesRepository
                    .getAlbumListSortPreferences()
                    .collectAsState(initial = null)
                    .value
            if (albums == null || sortPrefs == null) {
                Loading
            } else if (albums.isEmpty()) {
                Empty
            } else {
                Data(
                    AlbumListState(
                        albums = albums.map { album -> AlbumRow.State.fromAlbum(album) },
                        sortButtonState =
                            SortButton.State(
                                text = sortPrefs.sortOption.stringId,
                                sortOrder = sortPrefs.sortOrder,
                            ),
                    )
                )
            }
        }

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
                    TrackListUiComponent(
                        arguments = TrackListArguments(MediaGroup.Album(action.albumId)),
                        navController = navController,
                    )
                )
            }
        }
    }
}

fun getAlbumListStateHolder(
    dependencies: Dependencies,
    navController: NavController,
): AlbumListStateHolder {
    return AlbumListStateHolder(
        albumRepository = dependencies.repositoryProvider.albumRepository,
        navController = navController,
        sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
    )
}
