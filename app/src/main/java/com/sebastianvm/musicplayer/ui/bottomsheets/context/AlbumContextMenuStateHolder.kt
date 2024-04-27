package com.sebastianvm.musicplayer.ui.bottomsheets.context

import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.model.MediaWithArtists
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AlbumContextMenuStateHolder(
    stateHolderScope: CoroutineScope = stateHolderScope(),
    arguments: AlbumContextMenuArguments,
    albumRepository: AlbumRepository,
    private val playbackManager: PlaybackManager
) : BaseContextMenuStateHolder() {

    private var tracks: List<Track> = listOf()

    val albumId = arguments.albumId

    override val state: StateFlow<UiState<ContextMenuState>> =
        albumRepository.getFullAlbumInfo(albumId).map { album ->
            tracks = album.tracks
            Data(
                ContextMenuState(
                    menuTitle = album.album.albumName,
                    listItems = listOf(
                        ContextMenuItem.PlayFromBeginning,
                        ContextMenuItem.AddToQueue,
                        if (album.artists.size == 1) {
                            ContextMenuItem.ViewArtist(album.artists[0])
                        } else {
                            ContextMenuItem.ViewArtists(
                                mediaType = MediaWithArtists.Album,
                                mediaId = albumId
                            )
                        },
                        ContextMenuItem.ViewAlbum(albumId)
                    ),
                )
            )
        }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.AddToQueue -> {
                playbackManager.addToQueue(tracks)
            }

            else -> error("Invalid row for album context menu")
        }
    }
}

data class AlbumContextMenuArguments(val albumId: Long)
