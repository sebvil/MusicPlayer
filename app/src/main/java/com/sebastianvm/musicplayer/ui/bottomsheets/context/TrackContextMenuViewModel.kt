package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists.ArtistsMenuArguments
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
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
    private var artistIds: List<Long> = listOf()
    private var track: Track? = null

    init {
        trackRepository.getTrack(state.mediaId).onEach {
            artistIds = it.artists
            track = it.track
            setState {
                copy(
                    menuTitle = it.track.trackName,
                    listItems = when (state.mediaGroup.mediaGroupType) {
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
                with(state) {
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
                        MediaGroupType.ARTIST -> throw IllegalStateException(
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

            is ContextMenuItem.AddToQueue -> {
                track?.also { playbackManager.addToQueue(listOf(it)) }
            }

            is ContextMenuItem.ViewAlbum -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackList(
                            TrackListArguments(
                                trackListType = TrackListType.ALBUM,
                                trackListId = state.mediaGroup.mediaId
                            )
                        )
                    )
                )
            }

            ContextMenuItem.ViewArtist -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        destination = NavigationDestination.Artist(
                            arguments = ArtistArguments(artistId = artistIds[0])
                        )
                    )
                )
            }

            ContextMenuItem.ViewArtists -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        destination = NavigationDestination.ArtistsMenu(
                            arguments = ArtistsMenuArguments(artistIds = artistIds)
                        )
                    )
                )
            }

            ContextMenuItem.RemoveFromPlaylist -> {
                state.positionInPlaylist?.also {
                    viewModelScope.launch {
                        playlistRepository.removeItemFromPlaylist(
                            playlistId = state.mediaGroup.mediaId,
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