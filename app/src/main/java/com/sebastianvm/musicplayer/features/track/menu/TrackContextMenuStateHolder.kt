package com.sebastianvm.musicplayer.features.track.menu

import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistScreen
import com.sebastianvm.musicplayer.features.artistsmenu.ArtistsMenu
import com.sebastianvm.musicplayer.features.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.track.list.TrackList
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.model.MediaWithArtists
import com.sebastianvm.musicplayer.player.HasTracks
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TrackContextMenuArguments(
    val trackId: Long,
    val trackPositionInList: Int,
    val trackList: HasTracks
) : Arguments

sealed interface TrackContextMenuState : State {
    data class Data(
        val trackName: String,
        val trackId: Long,
        val viewArtistsState: ViewArtistRow,
        val viewAlbumState: ViewAlbumRow?,
        val removeFromPlaylistRow: RemoveFromPlaylistRow?,
    ) : TrackContextMenuState

    data object Loading : TrackContextMenuState
}

sealed interface ViewArtistRow {
    data class SingleArtist(val artistId: Long) : ViewArtistRow
    data object MultipleArtists : ViewArtistRow
    data object NoArtists : ViewArtistRow
}

data class ViewAlbumRow(val albumId: Long)
data class RemoveFromPlaylistRow(val playlistId: Long, val trackPositionInPlaylist: Long)

sealed interface TrackContextMenuUserAction : UserAction {
    data object AddToQueueClicked : TrackContextMenuUserAction
    data object ViewArtistsClicked : TrackContextMenuUserAction
    data class ViewArtistClicked(val artistId: Long) : TrackContextMenuUserAction
    data class ViewAlbumClicked(val albumId: Long) : TrackContextMenuUserAction
    data class RemoveFromPlaylistClicked(val playlistId: Long, val trackPositionInPlaylist: Long) :
        TrackContextMenuUserAction
}

class TrackContextMenuStateHolder(
    private val arguments: TrackContextMenuArguments,
    private val trackRepository: TrackRepository,
    private val playlistRepository: PlaylistRepository,
    private val playbackManager: PlaybackManager,
    private val navController: NavController,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<TrackContextMenuState, TrackContextMenuUserAction> {

    private val trackId = arguments.trackId

    override val state: StateFlow<TrackContextMenuState> =
        trackRepository.getTrack(trackId).map { track ->
            TrackContextMenuState.Data(
                trackName = track.track.trackName,
                trackId = trackId,
                viewArtistsState = when (track.artists.size) {
                    0 -> ViewArtistRow.NoArtists
                    1 -> ViewArtistRow.SingleArtist(track.artists[0])
                    else -> ViewArtistRow.MultipleArtists
                },
                viewAlbumState = if (arguments.trackList is MediaGroup.Album) {
                    null
                } else {
                    ViewAlbumRow(track.track.albumId)
                },
                removeFromPlaylistRow = (arguments.trackList as? MediaGroup.Playlist)?.let {
                    RemoveFromPlaylistRow(
                        playlistId = it.playlistId,
                        trackPositionInPlaylist = arguments.trackPositionInList.toLong()
                    )
                },
            )
        }.stateIn(stateHolderScope, SharingStarted.Lazily, TrackContextMenuState.Loading)

    override fun handle(action: TrackContextMenuUserAction) {
        when (action) {
            is TrackContextMenuUserAction.AddToQueueClicked -> {
                stateHolderScope.launch {
                    val track = trackRepository.getTrack(trackId).first().track
                    playbackManager.addToQueue(listOf(track))
                }
            }

            is TrackContextMenuUserAction.ViewAlbumClicked -> {
                navController.push(
                    TrackList(
                        arguments = TrackListArguments(
                            MediaGroup.Album(
                                albumId = action.albumId
                            )
                        ),
                        navController = navController
                    ),
                    navOptions = NavOptions(popCurrent = true)
                )
            }

            is TrackContextMenuUserAction.ViewArtistClicked -> {
                navController.push(
                    ArtistScreen(
                        arguments = ArtistArguments(artistId = action.artistId),
                        navController = navController
                    ),
                    navOptions = NavOptions(popCurrent = true)
                )
            }

            TrackContextMenuUserAction.ViewArtistsClicked -> {
                navController.push(
                    ArtistsMenu(
                        arguments = ArtistsMenuArguments(
                            mediaType = MediaWithArtists.Track,
                            mediaId = trackId
                        ),
                        navController = navController
                    ),
                    navOptions = NavOptions(
                        popCurrent = true,
                        NavOptions.PresentationMode.BottomSheet
                    )
                )
            }

            is TrackContextMenuUserAction.RemoveFromPlaylistClicked -> {
                stateHolderScope.launch {
                    playlistRepository.removeItemFromPlaylist(
                        playlistId = action.playlistId,
                        position = action.trackPositionInPlaylist
                    )
                }
            }
        }
    }
}

fun getTrackContextMenuStateHolder(
    dependencies: DependencyContainer,
    arguments: TrackContextMenuArguments,
    navController: NavController
): TrackContextMenuStateHolder {
    return TrackContextMenuStateHolder(
        arguments = arguments,
        trackRepository = dependencies.repositoryProvider.trackRepository,
        playlistRepository = dependencies.repositoryProvider.playlistRepository,
        playbackManager = dependencies.repositoryProvider.playbackManager,
        navController = navController
    )
}
