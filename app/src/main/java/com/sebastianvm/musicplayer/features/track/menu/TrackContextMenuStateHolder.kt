package com.sebastianvm.musicplayer.features.track.menu

import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.model.MediaWithArtists
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.Delegate
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

sealed interface SourceTrackList {

    data object AllTracks : SourceTrackList
    data object SearchResults : SourceTrackList
    data object Album : SourceTrackList
    data object Genre : SourceTrackList
    data class Playlist(
        val trackList: MediaGroup.Playlist,
        val trackPositionInPlaylist: Long
    ) : SourceTrackList
}

data class TrackContextMenuArguments(val trackId: Long, val trackList: SourceTrackList) : Arguments
interface TrackContextMenuDelegate : Delegate {
    fun showAlbum(arguments: TrackListArguments)
    fun showArtist(arguments: ArtistArguments)
    fun showArtists(arguments: ArtistsMenuArguments)
}

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
    data object PlayTrackClicked : TrackContextMenuUserAction
    data object ViewArtistsClicked : TrackContextMenuUserAction
    data class ViewArtistClicked(val artistId: Long) : TrackContextMenuUserAction
    data class ViewAlbumClicked(val albumId: Long) : TrackContextMenuUserAction
    data class RemoveFromPlaylistClicked(val playlistId: Long, val trackPositionInPlaylist: Long) :
        TrackContextMenuUserAction
}

class TrackContextMenuStateHolder(
    arguments: TrackContextMenuArguments,
    private val trackRepository: TrackRepository,
    private val playlistRepository: PlaylistRepository,
    private val playbackManager: PlaybackManager,
    private val delegate: TrackContextMenuDelegate,
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
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
                viewAlbumState = if (arguments.trackList is SourceTrackList.Album) {
                    null
                } else {
                    ViewAlbumRow(track.track.albumId)
                },
                removeFromPlaylistRow = (arguments.trackList as? SourceTrackList.Playlist)?.let {
                    RemoveFromPlaylistRow(
                        it.trackList.playlistId,
                        it.trackPositionInPlaylist
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

            TrackContextMenuUserAction.PlayTrackClicked -> {
            }

            is TrackContextMenuUserAction.ViewAlbumClicked -> {
                delegate.showAlbum(TrackListArguments(MediaGroup.Album(albumId = action.albumId)))
            }

            is TrackContextMenuUserAction.ViewArtistClicked -> {
                delegate.showArtist(ArtistArguments(artistId = action.artistId))
            }

            TrackContextMenuUserAction.ViewArtistsClicked -> {
                delegate.showArtists(
                    ArtistsMenuArguments(
                        mediaType = MediaWithArtists.Track,
                        mediaId = trackId
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