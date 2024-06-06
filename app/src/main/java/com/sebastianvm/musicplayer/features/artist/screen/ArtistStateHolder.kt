package com.sebastianvm.musicplayer.features.artist.screen

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.di.AppDependencies
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenu
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.features.track.list.TrackListUiComponent
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import com.sebastianvm.musicplayer.util.AlbumType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ArtistState(
    val artistName: String,
    val listItems: List<ArtistScreenItem>,
) : State

data class ArtistArguments(val artistId: Long) : Arguments

sealed interface ArtistUserAction : UserAction {
    data class AlbumMoreIconClicked(val albumId: Long) : ArtistUserAction
    data class AlbumClicked(val albumId: Long) : ArtistUserAction
    data object BackClicked : ArtistUserAction
}

class ArtistStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    arguments: ArtistArguments,
    artistRepository: ArtistRepository,
    private val navController: NavController,
) : StateHolder<UiState<ArtistState>, ArtistUserAction> {

    private val artistId = arguments.artistId

    override val state: StateFlow<UiState<ArtistState>> =
        artistRepository.getArtist(artistId).map { artistWithAlbums ->
            val listItems = buildList {
                if (artistWithAlbums.artistAlbums.isNotEmpty()) {
                    add(ArtistScreenItem.SectionHeaderItem(AlbumType.ALBUM))
                }
                addAll(artistWithAlbums.artistAlbums.map { album -> album.toAlbumRowItem() })
                if (artistWithAlbums.artistAppearsOn.isNotEmpty()) {
                    add(ArtistScreenItem.SectionHeaderItem(AlbumType.APPEARS_ON))
                }
                addAll(artistWithAlbums.artistAppearsOn.map { album -> album.toAlbumRowItem() })
            }
            if (listItems.isEmpty()) {
                Empty
            } else {
                Data(
                    ArtistState(
                        artistName = artistWithAlbums.artist.artistName,
                        listItems = listItems,
                    )
                )
            }
        }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: ArtistUserAction) {
        when (action) {
            is ArtistUserAction.AlbumMoreIconClicked -> {
                navController.push(
                    AlbumContextMenu(
                        arguments = AlbumContextMenuArguments(action.albumId),
                        navController = navController,
                    ),
                    navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet)
                )
            }

            is ArtistUserAction.BackClicked -> {
                navController.pop()
            }

            is ArtistUserAction.AlbumClicked -> {
                navController.push(
                    TrackListUiComponent(
                        arguments = TrackListArguments(MediaGroup.Album(albumId = action.albumId)),
                        navController = navController
                    )
                )
            }
        }
    }
}

private fun Album.toAlbumRowItem(): ArtistScreenItem.AlbumRowItem {
    return ArtistScreenItem.AlbumRowItem(this.toModelListItemState())
}

fun getArtistStateHolder(
    dependencies: AppDependencies,
    arguments: ArtistArguments,
    navController: NavController
): ArtistStateHolder {
    return ArtistStateHolder(
        arguments = arguments,
        artistRepository = dependencies.repositoryProvider.artistRepository,
        navController = navController,
    )
}
