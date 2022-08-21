package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.album.AlbumArguments
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists.ArtistsMenuArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.extensions.getArgs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AlbumContextMenuViewModel @Inject constructor(
    initialState: AlbumContextMenuState,
    albumRepository: AlbumRepository,
    private val playbackManager: PlaybackManager,
) : BaseContextMenuViewModel<AlbumContextMenuState>(initialState) {

    private var tracks: List<Track> = listOf()
    private var artistIds: List<Long> = listOf()

    init {
        albumRepository.getFullAlbumInfo(state.value.mediaId).onEach {
            artistIds = it.artists
            tracks = it.tracks
            setState {
                copy(
                    menuTitle = it.album.albumName,
                    listItems = listOf(
                        ContextMenuItem.PlayFromBeginning,
                        ContextMenuItem.AddToQueue,
                        if (it.artists.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                        ContextMenuItem.ViewAlbum
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.PlayFromBeginning -> {
                playbackManager.playAlbum(state.value.mediaId).onEach {
                    when (it) {
                        is PlaybackResult.Loading, is PlaybackResult.Error -> setState {
                            copy(
                                playbackResult = it
                            )
                        }
                        is PlaybackResult.Success -> addNavEvent(
                            NavEvent.NavigateToScreen(
                                NavigationDestination.MusicPlayer
                            )
                        )
                    }
                }.launchIn(viewModelScope)
            }
            is ContextMenuItem.AddToQueue -> {
                playbackManager.addToQueue(tracks)
            }
            is ContextMenuItem.ViewAlbum -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.Album(
                            AlbumArguments(
                                albumId = state.value.mediaId
                            )
                        )
                    )
                )
            }
            is ContextMenuItem.ViewArtists -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.ArtistsMenu(
                            ArtistsMenuArguments(artistIds = artistIds)
                        )
                    )
                )
            }
            is ContextMenuItem.ViewArtist -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.Artist(
                            ArtistArguments(
                                artistId = artistIds[0]
                            )
                        )
                    )
                )
            }
            else -> throw IllegalStateException("Invalid row for album context menu")
        }
    }

    override fun onPlaybackErrorDismissed() {
        setState { copy(playbackResult = null) }
    }
}

data class AlbumContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val mediaId: Long,
    override val menuTitle: String,
    override val playbackResult: PlaybackResult? = null,
) : BaseContextMenuState(listItems, mediaId, menuTitle, playbackResult)

@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialAlbumContextMenuStateProvider(savedStateHandle: SavedStateHandle): AlbumContextMenuState {
        val args = savedStateHandle.getArgs<AlbumContextMenuArguments>()
        return AlbumContextMenuState(
            mediaId = args.albumId,
            menuTitle = "",
            listItems = listOf(),
        )
    }
}


