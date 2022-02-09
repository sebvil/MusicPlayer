package com.sebastianvm.musicplayer.ui.artist

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.toAlbumRowState
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
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
) : BaseViewModel<ArtistUserAction, ArtistUiEvent, ArtistState>(
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

    override fun handle(action: ArtistUserAction) {
        when (action) {
            is ArtistUserAction.AlbumClicked -> {
                addUiEvent(ArtistUiEvent.NavigateToAlbum(action.albumId))
            }
            is ArtistUserAction.AlbumContextButtonClicked -> {
                addUiEvent(ArtistUiEvent.OpenContextMenu(action.albumId))
            }
            is ArtistUserAction.UpButtonClicked -> addUiEvent(ArtistUiEvent.NavigateUp)
        }
    }
}

data class ArtistState(
    val artistName: String,
    val albumsForArtistItems: List<ArtistScreenItem>?,
    val appearsOnForArtistItems: List<ArtistScreenItem>?,
) : State


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
            appearsOnForArtistItems = null
        )
    }
}

sealed class ArtistUserAction : UserAction {
    data class AlbumClicked(val albumId: String) : ArtistUserAction()
    data class AlbumContextButtonClicked(val albumId: String) : ArtistUserAction()
    object UpButtonClicked : ArtistUserAction()
}

sealed class ArtistUiEvent : UiEvent {
    data class NavigateToAlbum(val albumId: String) : ArtistUiEvent()
    data class OpenContextMenu(val albumId: String) : ArtistUiEvent()
    object NavigateUp : ArtistUiEvent()
}
