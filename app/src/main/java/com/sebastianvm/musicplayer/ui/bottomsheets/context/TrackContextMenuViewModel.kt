package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.model.MediaWithArtists
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
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
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrackContextMenuArguments(
    val trackId: Long,
    val mediaGroup: MediaGroup,
    val trackIndex: Int = 0,
    val positionInPlaylist: Long? = null
)

@InstallIn(ViewModelComponent::class)
@Module
object TrackContextMenuArgumentsModule {
    @Provides
    @ViewModelScoped
    fun trackContextMenuArgumentsProvider(savedStateHandle: SavedStateHandle): TrackContextMenuArguments {
        return savedStateHandle.navArgs()
    }
}

@HiltViewModel
class TrackContextMenuViewModel @Inject constructor(
    arguments: TrackContextMenuArguments,
    trackRepository: TrackRepository,
    private val playbackManager: PlaybackManager,
    private val playlistRepository: PlaylistRepository
) : BaseContextMenuViewModel() {
    private var artistIds: List<Long> = listOf()
    private lateinit var track: Track

    private val trackId = arguments.trackId
    private val mediaGroup = arguments.mediaGroup
    private val positionInPlaylist = arguments.positionInPlaylist

    init {
        trackRepository.getTrack(trackId).onEach { trackWithArtists ->
            artistIds = trackWithArtists.artists
            track = trackWithArtists.track
            setDataState {
                it.copy(
                    menuTitle = trackWithArtists.track.trackName,
                    listItems = when (arguments.mediaGroup) {
                        is MediaGroup.Album -> {
                            listOf(
                                ContextMenuItem.Play,
                                ContextMenuItem.AddToQueue,
                                ContextMenuItem.AddToPlaylist,
                                if (trackWithArtists.artists.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists
                            )
                        }

                        is MediaGroup.Playlist -> {
                            listOf(
                                ContextMenuItem.Play,
                                ContextMenuItem.AddToQueue,
                                ContextMenuItem.AddToPlaylist,
                                if (trackWithArtists.artists.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                                ContextMenuItem.ViewAlbum,
                                ContextMenuItem.RemoveFromPlaylist
                            )
                        }

                        else -> {
                            listOf(
                                ContextMenuItem.Play,
                                ContextMenuItem.AddToQueue,
                                ContextMenuItem.AddToPlaylist,
                                if (trackWithArtists.artists.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                                ContextMenuItem.ViewAlbum
                            )
                        }
                    }
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.AddToQueue -> {
                playbackManager.addToQueue(listOf(track))
            }

            is ContextMenuItem.ViewAlbum -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        TrackListRouteDestination(
                            TrackListArgumentsForNav(trackListType = MediaGroup.Album(albumId = track.albumId))
                        )
                    )
                )
            }

            is ContextMenuItem.ViewArtist -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        ArtistRouteDestination(ArtistArguments(artistId = artistIds[0]))
                    )
                )
            }

            is ContextMenuItem.ViewArtists -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        destination = ArtistsBottomSheetDestination(
                            navArgs = ArtistsMenuArguments(
                                mediaType = MediaWithArtists.Track,
                                mediaId = trackId
                            )
                        )
                    )
                )
            }

            is ContextMenuItem.RemoveFromPlaylist -> {
                positionInPlaylist?.also {
                    viewModelScope.launch {
                        check(mediaGroup is MediaGroup.Playlist)
                        playlistRepository.removeItemFromPlaylist(
                            playlistId = mediaGroup.playlistId,
                            position = it
                        )
                    }
                }
                addNavEvent(NavEvent.NavigateUp)
            }

            is ContextMenuItem.AddToPlaylist -> TODO()
            else -> error("Invalid row for track context menu")
        }
    }

    override fun onPlaybackErrorDismissed() {
        setDataState { it.copy(playbackResult = null) }
    }

    override val defaultState: ContextMenuState by lazy {
        ContextMenuState(
            menuTitle = "",
            listItems = listOf()
        )
    }
}
