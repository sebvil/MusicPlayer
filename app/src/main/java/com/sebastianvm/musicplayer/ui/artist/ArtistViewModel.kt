package com.sebastianvm.musicplayer.ui.artist

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists
import com.sebastianvm.musicplayer.repository.AlbumRepository
import com.sebastianvm.musicplayer.repository.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.ui.components.HeaderWithImageState
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.ArtLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@HiltViewModel
class ArtistViewModel @Inject constructor(
    initialState: ArtistState,
    artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
) : BaseViewModel<ArtistUserAction, ArtistUiEvent, ArtistState>(
    initialState
) {
    init {
        collect(artistRepository.getArtist(state.value.artistId)) { artistWithAlbums ->
            setState {
                copy(
                    artistHeaderItem = HeaderWithImageState(
                        MediaArt(
                            uris = listOf(),
                            contentDescription = DisplayableString.StringValue(""),
                            backupResource = com.sebastianvm.commons.R.drawable.ic_artist,
                            backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_artist_image)
                        ),
                        title = DisplayableString.StringValue(artistWithAlbums.artist.artistName)
                    ),
                )
            }
            artistWithAlbums.artistAlbums.takeUnless { it.isEmpty() }?.also { albums ->
                collect(albumRepository.getAlbums(albums.map { it.albumId })) { albumsWithArtists ->
                    setState {
                        copy(
                            albumsForArtistItems = listOf(
                                ArtistScreenItem.SectionHeaderItem(
                                    ALBUMS,
                                    R.string.albums
                                )
                            ).plus(albumsWithArtists.sortedByDescending { albumItem -> albumItem.album.year }
                                .map { it.toAlbumRowItem() })
                        )
                    }
                }
            } ?: kotlin.run {
                setState {
                    copy(
                        albumsForArtistItems = null
                    )
                }
            }
            artistWithAlbums.artistAppearsOn.takeUnless { it.isEmpty() }?.also { albums ->
                collect(albumRepository.getAlbums(albums.map { it.albumId })) { albumsWithArtists ->
                    setState {
                        copy(
                            appearsOnForArtistItems = listOf(
                                ArtistScreenItem.SectionHeaderItem(
                                    APPEARS_ON,
                                    R.string.appears_on
                                )
                            ).plus(albumsWithArtists.sortedByDescending { albumItem -> albumItem.album.year }
                                .map { it.toAlbumRowItem() })
                        )
                    }
                }
            } ?: kotlin.run {
                setState {
                    copy(
                        appearsOnForArtistItems = null
                    )
                }
            }
        }
    }

    private fun AlbumWithArtists.toAlbumRowItem(): ArtistScreenItem.AlbumRowItem {
        return ArtistScreenItem.AlbumRowItem(
            albumId = album.albumId,
            AlbumRowState(
                albumName = album.albumName,
                image = ArtLoader.getAlbumArt(
                    albumId = album.albumId.toLong(),
                    albumName = album.albumName
                ),
                year = album.year,
                artists = artists.joinToString(", ") { it.artistName }
            )
        )
    }

    override fun handle(action: ArtistUserAction) {
        when (action) {
            is ArtistUserAction.AlbumClicked -> {
                addUiEvent(ArtistUiEvent.NavigateToAlbum(action.albumId))
            }
            is ArtistUserAction.AlbumContextButtonClicked -> {
                addUiEvent(ArtistUiEvent.OpenContextMenu(action.albumId))
            }
        }
    }

    companion object {
        const val ALBUMS = "ALBUMS"
        const val APPEARS_ON = "APPEARS_ON"
    }

}

data class ArtistState(
    val artistHeaderItem: HeaderWithImageState,
    val artistId: String,
    val albumsForArtistItems: List<ArtistScreenItem>? = null,
    val appearsOnForArtistItems: List<ArtistScreenItem>? = null,
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistState {
    @Provides
    @ViewModelScoped
    fun provideInitialArtistState(savedStateHandle: SavedStateHandle): ArtistState {
        val artistId =
            savedStateHandle.get<String>(NavArgs.ARTIST_ID)!! // We should not get here without an id
        return ArtistState(
            artistHeaderItem = HeaderWithImageState(
                MediaArt(
                    uris = listOf(),
                    contentDescription = DisplayableString.StringValue(""),
                    backupResource = com.sebastianvm.commons.R.drawable.ic_artist,
                    backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_artist_image)
                ),
                title = DisplayableString.ResourceValue(R.string.unknown_artist)
            ),
            artistId = artistId
        )
    }
}

sealed class ArtistUserAction : UserAction {
    data class AlbumClicked(val albumId: String) : ArtistUserAction()
    data class AlbumContextButtonClicked(val albumId: String) : ArtistUserAction()
}

sealed class ArtistUiEvent : UiEvent {
    data class NavigateToAlbum(val albumId: String) : ArtistUiEvent()
    data class OpenContextMenu(val albumId: String) : ArtistUiEvent()

}

