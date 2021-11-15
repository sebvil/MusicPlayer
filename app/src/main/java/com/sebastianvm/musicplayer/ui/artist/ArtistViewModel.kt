package com.sebastianvm.musicplayer.ui.artist

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.ui.components.HeaderWithImageState
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.AlbumType
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
class ArtistViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    initialState: ArtistState
) : BaseViewModel<ArtistUserAction, ArtistState>(
    initialState
) {


    // TODO figure out if this is necessary with current impl/Media3
//    override fun onCleared() {
//        super.onCleared()
//        musicServiceConnection.unsubscribe("artist-${state.value.artistGid}")
//    }

    init {
        with(state.value) {
            musicServiceConnection.subscribe(
                "artist-$artistGid",
                object : MediaBrowserCompat.SubscriptionCallback() {
                    override fun onChildrenLoaded(
                        parentId: String,
                        children: MutableList<MediaBrowserCompat.MediaItem>
                    ) {
                        super.onChildrenLoaded(parentId, children)

                        val albumRowItems =
                            children.mapNotNull { child -> child.description.toAlbumRowItem() }

                        setState {
                            copy(
                                albumsForArtistItems = albumRowItems.filter { it.albumType == AlbumType.ALBUM }
                                    .let {
                                        if (it.isEmpty()) {
                                            null
                                        } else {
                                            listOf(
                                                ArtistScreenItem.SectionHeaderItem(
                                                    ALBUMS,
                                                    R.string.albums
                                                )
                                            ).plus(it)
                                        }
                                    },
                                appearsOnForArtistItems = albumRowItems.filter { it.albumType == AlbumType.APPEARS_ON }
                                    .let {
                                        if (it.isEmpty()) {
                                            null
                                        } else {
                                            listOf(
                                                ArtistScreenItem.SectionHeaderItem(
                                                    APPEARS_ON,
                                                    R.string.appears_on
                                                )
                                            ).plus(it)
                                        }
                                    },
                            )
                        }
                    }
                }

            )


        }
    }

    fun MediaDescriptionCompat.toAlbumRowItem(): ArtistScreenItem.AlbumRowItem? {
        val meta =
            extras?.getParcelable<MediaMetadataCompat>(MEDIA_METADATA_COMPAT_KEY) ?: return null
        val id = meta.id ?: return null
        val albumName = meta.album ?: return null
        val artists =
            meta.albumArtists ?: return null
        val year = meta.year
        val albumType = meta.albumType ?: return null
        return ArtistScreenItem.AlbumRowItem(
            id,
            albumType,
            AlbumRowState(
                albumName = albumName,
                image = ArtLoader.getAlbumArt(albumGid = id.toLong(), albumName = albumName),
                year = year,
                artists = artists
            )
        )
    }

    override fun handle(action: ArtistUserAction) = Unit

    companion object {
        const val ALBUMS = "ALBUMS"
        const val APPEARS_ON = "APPEARS_ON"
    }

}

data class ArtistState(
    val artistHeaderItem: HeaderWithImageState,
    val artistGid: String,
    val albumsForArtistItems: List<ArtistScreenItem>? = null,
    val appearsOnForArtistItems: List<ArtistScreenItem>? = null,
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistState {
    @Provides
    @ViewModelScoped
    fun provideInitialArtistState(savedStateHandle: SavedStateHandle): ArtistState {
        val artistGid =
            savedStateHandle.get<String>(NavArgs.ARTIST_GID)!! // We should not get here without an id
        val artistName = savedStateHandle.get<String>(NavArgs.ARTIST_NAME)
        return ArtistState(
            artistHeaderItem = HeaderWithImageState(
                MediaArt(
                    uris = listOf(),
                    contentDescription = DisplayableString.StringValue(""),
                    backupResource = com.sebastianvm.commons.R.drawable.ic_artist,
                    backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_artist_image)
                ),
                title = artistName?.let { DisplayableString.StringValue(it) }
                    ?: DisplayableString.ResourceValue(R.string.unknown_artist)
            ),
            artistGid = artistGid
        )
    }
}

sealed class ArtistUserAction : UserAction


