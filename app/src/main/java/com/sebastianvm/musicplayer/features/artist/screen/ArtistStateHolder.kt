package com.sebastianvm.musicplayer.features.artist.screen

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuDelegate
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuStateHolder
import com.sebastianvm.musicplayer.features.album.menu.albumContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.Screen
import com.sebastianvm.musicplayer.features.track.list.TrackList
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Delegate
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolder
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import com.sebastianvm.musicplayer.util.AlbumType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

interface ArtistScreenDelegate : Delegate, NavController

data class ArtistState(
    val artistName: String,
    val listItems: List<ArtistScreenItem>,
    val albumContextMenuStateHolder: AlbumContextMenuStateHolder?
) : State

data class ArtistArguments(val artistId: Long) : Arguments

sealed interface ArtistUserAction : UserAction {
    data class AlbumMoreIconClicked(val albumId: Long) : ArtistUserAction
    data class AlbumClicked(val albumId: Long) : ArtistUserAction
    data object AlbumContextMenuDismissed : ArtistUserAction
    data object BackClicked : ArtistUserAction
}

class ArtistStateHolder(
    stateHolderScope: CoroutineScope = stateHolderScope(),
    arguments: ArtistArguments,
    artistRepository: ArtistRepository,
    private val delegate: ArtistScreenDelegate,
    private val albumContextMenuStateHolderFactory:
    StateHolderFactory<AlbumContextMenuArguments, AlbumContextMenuDelegate, AlbumContextMenuStateHolder>
) : StateHolder<UiState<ArtistState>, ArtistUserAction> {

    private val artistId = arguments.artistId
    private val _contextMenuAlbumId: MutableStateFlow<Long?> = MutableStateFlow(null)

    override val state: StateFlow<UiState<ArtistState>> =
        combine(
            artistRepository.getArtist(artistId),
            _contextMenuAlbumId
        ) { artistWithAlbums, contextMenuAlbumId ->
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
                        albumContextMenuStateHolder = contextMenuAlbumId?.let { albumId ->
                            albumContextMenuStateHolderFactory.getStateHolder(
                                arguments = AlbumContextMenuArguments(albumId = albumId),
                                delegate = object : AlbumContextMenuDelegate {
                                    override fun push(screen: Screen<*>) {
                                        _contextMenuAlbumId.update { null }
                                        push(screen)
                                    }

                                    override fun pop() {
                                        _contextMenuAlbumId.update { null }
                                    }
                                }
                            )
                        }
                    )
                )
            }
        }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: ArtistUserAction) {
        when (action) {
            is ArtistUserAction.AlbumContextMenuDismissed -> {
                _contextMenuAlbumId.update { null }
            }

            is ArtistUserAction.AlbumMoreIconClicked -> {
                _contextMenuAlbumId.update { action.albumId }
            }

            is ArtistUserAction.BackClicked -> {
                delegate.pop()
            }

            is ArtistUserAction.AlbumClicked -> {
                delegate.push(
                    TrackList(
                        arguments = TrackListArguments(MediaGroup.Album(albumId = action.albumId)),
                        navController = delegate
                    )
                )
            }
        }
    }
}

private fun Album.toAlbumRowItem(): ArtistScreenItem.AlbumRowItem {
    return ArtistScreenItem.AlbumRowItem(this.toModelListItemState())
}

@Composable
fun rememberArtistStateHolder(
    arguments: ArtistArguments,
    navController: NavController
): ArtistStateHolder {
    val albumContextMenuStateHolderFactory = albumContextMenuStateHolderFactory()
    return stateHolder { dependencyContainer ->
        ArtistStateHolder(
            arguments = arguments,
            artistRepository = dependencyContainer.repositoryProvider.artistRepository,
            delegate = object : ArtistScreenDelegate, NavController by navController {},
            albumContextMenuStateHolderFactory = albumContextMenuStateHolderFactory
        )
    }
}
