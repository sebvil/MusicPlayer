package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
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
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrackContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val mediaId: Long,
    override val menuTitle: String,
    override val playbackResult: PlaybackResult? = null,
    val mediaGroup: MediaGroup,
    val trackIndex: Int,
    val positionInPlaylist: Long? = null
) : BaseContextMenuState(listItems, mediaId, menuTitle, playbackResult)

@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialTrackContextMenuStateProvider(savedStateHandle: SavedStateHandle): TrackContextMenuState {
        val args = savedStateHandle.getArgs<TrackContextMenuArguments>()
        return TrackContextMenuState(
            mediaId = args.trackId,
            menuTitle = "",
            mediaGroup = args.mediaGroup,
            listItems = listOf(),
            trackIndex = args.trackIndex,
            positionInPlaylist = args.positionInPlaylist
        )
    }
}

@HiltViewModel
class TrackContextMenuViewModel @Inject constructor(
    initialState: TrackContextMenuState,
    trackRepository: TrackRepository,
    private val playbackManager: PlaybackManager,
    private val playlistRepository: PlaylistRepository
) : BaseContextMenuViewModel<TrackContextMenuState>(initialState) {
    private var artistId: Long = 0

    init {
        trackRepository.getTrack(state.value.mediaId).onEach {
            if (it.artists.size == 1) {
                artistId = it.artists[0]
            }
            setState {
                copy(
                    menuTitle = it.track.trackName,
                    listItems = when (state.value.mediaGroup.mediaGroupType) {
                        MediaGroupType.ALBUM -> {
                            listOf(
                                ContextMenuItem.Play,
                                ContextMenuItem.AddToQueue,
                                if (it.artists.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                            )
                        }
                        MediaGroupType.PLAYLIST -> {
                            listOf(
                                ContextMenuItem.Play,
                                ContextMenuItem.AddToQueue,
                                if (it.artists.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                                ContextMenuItem.ViewAlbum,
                                ContextMenuItem.RemoveFromPlaylist
                            )
                        }
                        else -> {
                            listOf(
                                ContextMenuItem.Play,
                                ContextMenuItem.AddToQueue,
                                if (it.artists.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                                ContextMenuItem.ViewAlbum
                            )
                        }
                    },
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.Play -> {
                with(state.value) {
                    val playMediaFlow = when (mediaGroup.mediaGroupType) {
                        MediaGroupType.ALL_TRACKS -> {
                            playbackManager.playAllTracks(trackIndex)
                        }
                        MediaGroupType.GENRE -> {
                            playbackManager.playGenre(
                                genreId = mediaGroup.mediaId,
                                initialTrackIndex = trackIndex
                            )
                        }
                        MediaGroupType.ALBUM -> {
                            playbackManager.playAlbum(
                                albumId = mediaGroup.mediaId,
                                initialTrackIndex = trackIndex
                            )
                        }
                        MediaGroupType.PLAYLIST -> {
                            playbackManager.playPlaylist(
                                playlistId = mediaGroup.mediaId
                            )
                        }
                        MediaGroupType.SINGLE_TRACK -> playbackManager.playSingleTrack(mediaId)
                        MediaGroupType.ARTIST, MediaGroupType.UNKNOWN -> throw IllegalStateException(
                            "Unsupported media group type: ${mediaGroup.mediaGroupType}"
                        )
                    }
                    playMediaFlow.onEach {
                        when (it) {
                            is PlaybackResult.Loading, is PlaybackResult.Error -> setState {
                                copy(
                                    playbackResult = it
                                )
                            }
                            is PlaybackResult.Success -> {
                                addNavEvent(NavEvent.NavigateToScreen(destination = NavigationDestination.MusicPlayer))
                            }
                        }
                    }.launchIn(viewModelScope)
                }

            }
            ContextMenuItem.AddToQueue -> {
                viewModelScope.launch {
                    playbackManager.addToQueue(listOf(state.value.mediaId))
                }
            }
            ContextMenuItem.ViewAlbum -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        destination = NavigationDestination.Album(
                            arguments = AlbumArguments(albumId = state.value.mediaGroup.mediaId)
                        )
                    )
                )
            }
            ContextMenuItem.ViewArtist -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        destination = NavigationDestination.Artist(
                            arguments = ArtistArguments(artistId = artistId)
                        )
                    )
                )
            }
            ContextMenuItem.ViewArtists -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        destination = NavigationDestination.ArtistsMenu(
                            arguments = ArtistsMenuArguments(
                                mediaId = state.value.mediaId,
                                mediaType = MediaType.TRACK
                            )
                        )
                    )
                )
            }
            ContextMenuItem.RemoveFromPlaylist -> {
                state.value.positionInPlaylist?.also {
                    viewModelScope.launch {
                        playlistRepository.removeItemFromPlaylist(
                            playlistId = state.value.mediaGroup.mediaId,
                            position = it
                        )
                    }
                }
                addNavEvent(NavEvent.NavigateUp)

            }
            else -> throw IllegalStateException("Invalid row for track context menu")
        }
    }

    override fun onPlaybackErrorDismissed() {
        setState { copy(playbackResult = null) }
    }
}