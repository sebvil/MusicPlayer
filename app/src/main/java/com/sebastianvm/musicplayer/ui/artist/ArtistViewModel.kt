package com.sebastianvm.musicplayer.ui.artist

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.toAlbumRowState
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.AlbumType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ArtistViewModel @Inject constructor(
    initialState: ArtistState,
    artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
) : BaseViewModel<ArtistUiEvent, ArtistState>(
    initialState
) {
    init {
        collect(
            artistRepository.getArtist(state.value.artistName).flatMapLatest { artistWithAlbums ->
                val albumsForArtist = artistWithAlbums.artistAlbums.let { albums ->
                    albumRepository.getAlbums(albums)
                }
                val appearsOnForArtist = artistWithAlbums.artistAppearsOn.let { albums ->
                    albumRepository.getAlbums(albums)
                }
                albumsForArtist.combine(appearsOnForArtist) { albumsFor, appearsOn ->
                    Pair(
                        albumsFor,
                        appearsOn
                    )
                }
            }) { (albumsForArtist, appearsOnForArtist) ->
            setState {
                copy(
                    albumsForArtistItems = albumsForArtist.takeUnless { it.isEmpty() }
                        ?.let { albums ->
                            listOf(
                                ArtistScreenItem.SectionHeaderItem(
                                    AlbumType.ALBUM,
                                    R.string.albums
                                )
                            ).plus(albums.sortedByDescending { album -> album.year }
                                .map { it.toAlbumRowItem() })

                        },
                    appearsOnForArtistItems = appearsOnForArtist.takeUnless { it.isEmpty() }
                        ?.let { albums ->
                            listOf(
                                ArtistScreenItem.SectionHeaderItem(
                                    AlbumType.APPEARS_ON,
                                    R.string.appears_on
                                )
                            ).plus(albums.sortedByDescending { album -> album.year }
                                .map { it.toAlbumRowItem() })
                        },
                )
            }
        }
    }

    private fun Album.toAlbumRowItem(): ArtistScreenItem.AlbumRowItem {
        return ArtistScreenItem.AlbumRowItem(this.toAlbumRowState())
    }

    fun onAlbumClicked(albumId: String) {
        addUiEvent(ArtistUiEvent.NavigateToAlbum(albumId))
    }

    fun onAlbumOverflowMenuIconClicked(albumId: String) {
        addUiEvent(ArtistUiEvent.OpenContextMenu(albumId))
    }

    fun onUpButtonClicked() {
        addUiEvent(ArtistUiEvent.NavigateUp)
    }

}

data class ArtistState(
    val artistName: String,
    val albumsForArtistItems: List<ArtistScreenItem>?,
    val appearsOnForArtistItems: List<ArtistScreenItem>?,
    override val events: ArtistUiEvent?,
) : State<ArtistUiEvent> {

    @Suppress("UNCHECKED_CAST")
    override fun <S : State<ArtistUiEvent>> setEvent(event: ArtistUiEvent?): S {
        return copy(events = event) as S
    }
}

@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistState {
    @Provides
    @ViewModelScoped
    fun provideInitialArtistState(savedStateHandle: SavedStateHandle): ArtistState {
        val artistName =
            savedStateHandle.get<String>(NavArgs.ARTIST_ID)!! // We should not get here without an id
        return ArtistState(
            artistName = artistName,
            albumsForArtistItems = null,
            appearsOnForArtistItems = null,
            events = null
        )
    }
}

sealed class ArtistUiEvent : UiEvent {
    data class NavigateToAlbum(val albumId: String) : ArtistUiEvent()
    data class OpenContextMenu(val albumId: String) : ArtistUiEvent()
    object NavigateUp : ArtistUiEvent()
}
