package com.sebastianvm.musicplayer.features.artist.screen

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.designsystem.components.AlbumRow
import com.sebastianvm.musicplayer.features.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.album.details.AlbumDetailsUiComponent
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenu
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import com.sebastianvm.resources.RString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ArtistScreenSection(@StringRes val title: Int, val albums: List<AlbumRow.State>)

sealed interface ArtistState : State {
    data class Data(
        val artistName: String,
        val artistAlbumsSection: ArtistScreenSection?,
        val artistAppearsOnSection: ArtistScreenSection?,
    ) : ArtistState

    data object Loading : ArtistState
}

data class ArtistArguments(val artistId: Long) : Arguments

sealed interface ArtistUserAction : UserAction {
    data class AlbumMoreIconClicked(val albumId: Long) : ArtistUserAction

    data class AlbumClicked(val albumItem: AlbumRow.State) : ArtistUserAction

    data object BackClicked : ArtistUserAction
}

class ArtistStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    arguments: ArtistArguments,
    artistRepository: ArtistRepository,
    private val navController: NavController,
) : StateHolder<ArtistState, ArtistUserAction> {

    private val artistId = arguments.artistId

    override val state: StateFlow<ArtistState> =
        artistRepository
            .getArtist(artistId)
            .map { artistWithAlbums ->
                ArtistState.Data(
                    artistName = artistWithAlbums.name,
                    artistAlbumsSection =
                        artistWithAlbums.albums
                            .takeIf { it.isNotEmpty() }
                            ?.let { albums ->
                                ArtistScreenSection(
                                    title = RString.albums,
                                    albums = albums.map { AlbumRow.State.fromAlbum(it) },
                                )
                            },
                    artistAppearsOnSection =
                        artistWithAlbums.appearsOn
                            .takeIf { it.isNotEmpty() }
                            ?.let { albums ->
                                ArtistScreenSection(
                                    title = RString.appears_on,
                                    albums = albums.map { AlbumRow.State.fromAlbum(it) },
                                )
                            },
                )
            }
            .stateIn(stateHolderScope, SharingStarted.Lazily, ArtistState.Loading)

    override fun handle(action: ArtistUserAction) {
        when (action) {
            is ArtistUserAction.AlbumMoreIconClicked -> {
                navController.push(
                    AlbumContextMenu(
                        arguments = AlbumContextMenuArguments(action.albumId),
                        navController = navController,
                    ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is ArtistUserAction.BackClicked -> {
                navController.pop()
            }
            is ArtistUserAction.AlbumClicked -> {
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
