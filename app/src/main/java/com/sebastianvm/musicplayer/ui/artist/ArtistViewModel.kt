package com.sebastianvm.musicplayer.ui.artist

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.album.AlbumArguments
import com.sebastianvm.musicplayer.ui.components.toAlbumRowState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.AlbumType
import com.sebastianvm.musicplayer.util.extensions.getArgs
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
    // TODO this is broken
    init {
        collect(
            artistRepository.getArtist(state.value.artistId).flatMapLatest { artistWithAlbums ->
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

    fun onAlbumClicked(albumId: Long) {
        addUiEvent(
            ArtistUiEvent.NavEvent(
                NavigationDestination.AlbumDestination(
                    AlbumArguments(
                        albumId = albumId
                    )
                )
            )
        )
    }

    fun onAlbumOverflowMenuIconClicked(albumId: Long) {
        addUiEvent(ArtistUiEvent.OpenContextMenu(albumId))
    }

    fun onUpButtonClicked() {
        addUiEvent(ArtistUiEvent.NavigateUp)
    }

}

data class ArtistState(
    val artistId: Long,
    val artistName: String,
    val albumsForArtistItems: List<ArtistScreenItem>?,
    val appearsOnForArtistItems: List<ArtistScreenItem>?
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistState {
    @Provides
    @ViewModelScoped
    fun provideInitialArtistState(savedStateHandle: SavedStateHandle): ArtistState {
        val args = savedStateHandle.getArgs<ArtistArguments>()
        return ArtistState(
            artistId = args.artistId,
            artistName = "",
            albumsForArtistItems = null,
            appearsOnForArtistItems = null,
        )
    }
}

sealed class ArtistUiEvent : UiEvent {
    data class NavEvent(val navigationDestination: NavigationDestination) : ArtistUiEvent()
    data class OpenContextMenu(val albumId: Long) : ArtistUiEvent()
    object NavigateUp : ArtistUiEvent()
}
