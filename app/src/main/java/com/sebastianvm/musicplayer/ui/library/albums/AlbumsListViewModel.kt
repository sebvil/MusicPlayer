package com.sebastianvm.musicplayer.ui.library.albums

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.player.BrowseTree
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.ArtLoader
import com.sebastianvm.musicplayer.util.extensions.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@HiltViewModel
class AlbumsListViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection,
    initialState: AlbumsListState
) : BaseViewModel<AlbumsListUserAction, AlbumsListState>(initialState) {

    init {
        musicServiceConnection.subscribe(
            BrowseTree.ALBUMS_ROOT,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    setState {
                        copy(
                            albumsList = children.mapNotNull { child ->
                                child.description.toAlbumsListItem()
                            }.sortedBy { it.albumRowState.albumName },
                        )
                    }

                }
            }
        )
    }


    fun MediaDescriptionCompat.toAlbumsListItem(): AlbumsListItem? {
        val meta =
            extras?.getParcelable<MediaMetadataCompat>(MEDIA_METADATA_COMPAT_KEY) ?: return null
        val id = meta.id ?: return null
        val albumName = meta.album ?: return null
        val artists =
            meta.albumArtists ?: return null
        val year = meta.year
        return AlbumsListItem(
            id,
            AlbumRowState(
                albumName = albumName,
                image = ArtLoader.getAlbumArt(albumGid = id.toLong(), albumName = albumName),
                artists = artists,
                year = year
            )
        )
    }

    override fun handle(action: AlbumsListUserAction) = Unit
}

data class AlbumsListState(
    val albumsList: List<AlbumsListItem>
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumsListStateModule {
    @Provides
    @ViewModelScoped
    fun initialAlbumsStateProvider(): AlbumsListState {
        return AlbumsListState(
            albumsList = listOf()
        )
    }
}

sealed class AlbumsListUserAction : UserAction

