package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.model.MediaWithArtists
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists.ArtistsMenuArguments
import com.sebastianvm.musicplayer.ui.destinations.ArtistRouteDestination
import com.sebastianvm.musicplayer.ui.destinations.ArtistsBottomSheetDestination
import com.sebastianvm.musicplayer.ui.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.ui.navArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
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
    arguments: AlbumContextMenuArguments,
    albumRepository: AlbumRepository,
    private val playbackManager: PlaybackManager
) : BaseContextMenuViewModel() {

    private var tracks: List<Track> = listOf()
    private var artistIds: List<Long> = listOf()

    val albumId = arguments.albumId

    init {
        albumRepository.getFullAlbumInfo(albumId).onEach { album ->
            artistIds = album.artists
            tracks = album.tracks
            setDataState {
                it.copy(
                    menuTitle = album.album.albumName,
                    listItems = listOf(
                        ContextMenuItem.PlayFromBeginning,
                        ContextMenuItem.AddToQueue,
                        if (album.artists.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                        ContextMenuItem.ViewAlbum
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.PlayFromBeginning -> {
                playbackManager.playAlbum(albumId).onEach { result ->
                    when (result) {
                        is PlaybackResult.Loading, is PlaybackResult.Error -> {
                            setDataState {
                                it.copy(
                                    playbackResult = result
                                )
                            }
                        }

                        is PlaybackResult.Success -> {}
                    }
                }.launchIn(viewModelScope)
            }

            is ContextMenuItem.AddToQueue -> {
                playbackManager.addToQueue(tracks)
            }

            is ContextMenuItem.ViewAlbum -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        TrackListRouteDestination(
                            TrackListArgumentsForNav(
                                trackListType = MediaGroup.Album(
                                    albumId
                                )
                            )
                        )
                    )
                )
            }

            is ContextMenuItem.ViewArtists -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        ArtistsBottomSheetDestination(
                            ArtistsMenuArguments(
                                mediaType = MediaWithArtists.Album,
                                mediaId = albumId
                            )
                        )
                    )
                )
            }

            is ContextMenuItem.ViewArtist -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        ArtistRouteDestination(
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
        setDataState { it.copy(playbackResult = null) }
    }

    override val defaultState: ContextMenuState by lazy {
        ContextMenuState(listItems = listOf(), menuTitle = "", playbackResult = null)
    }
}

data class AlbumContextMenuArguments(val albumId: Long)

@InstallIn(ViewModelComponent::class)
@Module
object AlbumContextMenuArgumentsModule {
    @Provides
    @ViewModelScoped
    fun albumContextMenuArgumentsProvider(savedStateHandle: SavedStateHandle): AlbumContextMenuArguments {
        return savedStateHandle.navArgs()
    }
}
