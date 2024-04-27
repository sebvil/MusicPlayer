package com.sebastianvm.musicplayer.ui.bottomsheets.context

import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.model.MediaWithArtists
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TrackContextMenuArguments(
    val trackId: Long,
    val mediaGroup: MediaGroup,
    val trackIndex: Int = 0,
    val positionInPlaylist: Long? = null
)

class TrackContextMenuStateHolder(
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
    arguments: TrackContextMenuArguments,
    trackRepository: TrackRepository,
    private val playbackManager: PlaybackManager,
    private val playlistRepository: PlaylistRepository
) : BaseContextMenuStateHolder() {
    private var artistIds: List<Long> = listOf()
    private lateinit var track: Track

    private val trackId = arguments.trackId
    private val mediaGroup = arguments.mediaGroup
    private val positionInPlaylist = arguments.positionInPlaylist

    override val state: StateFlow<UiState<ContextMenuState>> =
        trackRepository.getTrack(trackId).map { trackWithArtists ->
            artistIds = trackWithArtists.artists
            track = trackWithArtists.track
            val viewArtistsItem = if (artistIds.size == 1) {
                ContextMenuItem.ViewArtist(artistIds[0])
            } else {
                ContextMenuItem.ViewArtists(
                    mediaType = MediaWithArtists.Track,
                    mediaId = trackId
                )
            }
            Data(
                ContextMenuState(
                    menuTitle = trackWithArtists.track.trackName,
                    listItems = when (arguments.mediaGroup) {
                        is MediaGroup.Album -> {
                            listOf(
                                ContextMenuItem.Play,
                                ContextMenuItem.AddToQueue,
                                ContextMenuItem.AddToPlaylist,
                                viewArtistsItem,
                            )
                        }

                        is MediaGroup.Playlist -> {
                            listOf(
                                ContextMenuItem.Play,
                                ContextMenuItem.AddToQueue,
                                ContextMenuItem.AddToPlaylist,
                                viewArtistsItem,
                                ContextMenuItem.ViewAlbum(track.albumId),
                                ContextMenuItem.RemoveFromPlaylist
                            )
                        }

                        else -> {
                            listOf(
                                ContextMenuItem.Play,
                                ContextMenuItem.AddToQueue,
                                ContextMenuItem.AddToPlaylist,
                                viewArtistsItem,
                                ContextMenuItem.ViewAlbum(track.albumId)
                            )
                        }
                    }
                )
            )
        }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.AddToQueue -> {
                playbackManager.addToQueue(listOf(track))
            }

            is ContextMenuItem.RemoveFromPlaylist -> {
                positionInPlaylist?.also {
                    stateHolderScope.launch {
                        check(mediaGroup is MediaGroup.Playlist)
                        playlistRepository.removeItemFromPlaylist(
                            playlistId = mediaGroup.playlistId,
                            position = it
                        )
                    }
                }
            }

            is ContextMenuItem.AddToPlaylist -> TODO()
            else -> error("Invalid row for track context menu")
        }
    }
}
