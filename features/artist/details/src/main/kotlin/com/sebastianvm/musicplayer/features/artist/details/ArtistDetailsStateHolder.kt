package com.sebastianvm.musicplayer.features.artist.details

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
import com.sebastianvm.musicplayer.core.designsystems.components.AlbumRow
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.stateHolderScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.api.album.details.albumDetails
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.api.album.menu.albumContextMenu
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ArtistDetailsSection(@StringRes val title: Int, val albums: List<AlbumRow.State>)

sealed interface ArtistDetailsState : State {
    data class Data(
        val artistName: String,
        val artistAlbumsSection: ArtistDetailsSection?,
        val artistAppearsOnSection: ArtistDetailsSection?,
    ) : ArtistDetailsState

    data object Loading : ArtistDetailsState
}

sealed interface ArtistDetailsUserAction : UserAction {
    data class AlbumMoreIconClicked(val albumId: Long) : ArtistDetailsUserAction

    data class AlbumClicked(val albumItem: AlbumRow.State) : ArtistDetailsUserAction

    data object BackClicked : ArtistDetailsUserAction
}

class ArtistDetailsStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    arguments: ArtistDetailsArguments,
    artistRepository: ArtistRepository,
    private val navController: NavController,
    private val features: FeatureRegistry,
) : StateHolder<ArtistDetailsState, ArtistDetailsUserAction> {

    private val artistId = arguments.artistId

    override val state: StateFlow<ArtistDetailsState> =
        artistRepository
            .getArtist(artistId)
            .map { artistWithAlbums ->
                ArtistDetailsState.Data(
                    artistName = artistWithAlbums.name,
                    artistAlbumsSection =
                        artistWithAlbums.albums
                            .takeIf { it.isNotEmpty() }
                            ?.let { albums ->
                                ArtistDetailsSection(
                                    title = RString.albums,
                                    albums = albums.map { AlbumRow.State.fromAlbum(it) },
                                )
                            },
                    artistAppearsOnSection =
                        artistWithAlbums.appearsOn
                            .takeIf { it.isNotEmpty() }
                            ?.let { albums ->
                                ArtistDetailsSection(
                                    title = RString.appears_on,
                                    albums = albums.map { AlbumRow.State.fromAlbum(it) },
                                )
                            },
                )
            }
            .stateIn(stateHolderScope, SharingStarted.Lazily, ArtistDetailsState.Loading)

    override fun handle(action: ArtistDetailsUserAction) {
        when (action) {
            is ArtistDetailsUserAction.AlbumMoreIconClicked -> {
                navController.push(
                    features
                        .albumContextMenu()
                        .albumContextMenuUiComponent(
                            arguments = AlbumContextMenuArguments(action.albumId),
                            navController = navController,
                        ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is ArtistDetailsUserAction.BackClicked -> {
                navController.pop()
            }
            is ArtistDetailsUserAction.AlbumClicked -> {
                navController.push(
                    features
                        .albumDetails()
                        .albumDetailsUiComponent(
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
