package com.sebastianvm.musicplayer.repository.playback.mediatree

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_ALBUMS
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_MIXED
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_NONE
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_TITLES
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.uri.UriUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MediaTree @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackRepository: TrackRepository,
    private val albumRepository: AlbumRepository
) {


    private fun buildMediaItem(
        title: String,
        mediaId: String,
        isPlayable: Boolean,
        @MediaMetadata.FolderType folderType: Int,
        album: String? = null,
        artist: String? = null,
        genre: String? = null,
        sourceUri: Uri? = null,
        imageUri: Uri? = null,
    ): MediaItem {
        val metadata =
            MediaMetadata.Builder()
                .setAlbumTitle(album)
                .setTitle(title)
                .setSubtitle(artist)
                .setArtist(artist)
                .setGenre(genre)
                .setFolderType(folderType)
                .setIsPlayable(isPlayable)
                .setArtworkUri(imageUri)
                .build()
        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setMediaMetadata(metadata)
            .setUri(sourceUri)
            .build()
    }

    fun Context.resourceUri(resourceId: Int): Uri = with(resources) {
        Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(getResourcePackageName(resourceId))
            .appendPath(getResourceTypeName(resourceId))
            .appendPath(getResourceEntryName(resourceId))
            .build()
    }

    private fun Track.toMediaItemExternal(): MediaItem {
        return buildMediaItem(
            title = trackName,
            mediaId = trackId,
            isPlayable = true,
            folderType = FOLDER_TYPE_NONE,
            album = albumName,
            artist = artists,
            genre = "",
            sourceUri = UriUtils.getTrackUri(trackId = trackId.toLong()),
            imageUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, trackId.toLong())
        )
    }

    private fun Album.toMediaItemExternal(): MediaItem {
        return buildMediaItem(
            title = albumName,
            mediaId = "ALBUM-$albumId",
            isPlayable = false,
            folderType = FOLDER_TYPE_TITLES,
            album = albumName,
            artist = artists,
            genre = "",
            sourceUri = UriUtils.getAlbumUri(albumId = albumId.toLong()),
            imageUri = UriUtils.getAlbumUri(albumId = albumId.toLong())
        )
    }

    fun getRoot(): MediaItem {
        return buildMediaItem(
            title = "Root folder",
            mediaId = ROOT,
            isPlayable = false,
            folderType = FOLDER_TYPE_MIXED
        )
    }

    suspend fun getChildren(parent: String): List<MediaItem>? {
        return when {
            parent == ROOT -> {
                listOf(
                    buildMediaItem(
                        title = "All tracks",
                        mediaId = ALL_TRACKS,
                        isPlayable = false,
                        folderType = FOLDER_TYPE_TITLES
                    ),
                    buildMediaItem(
                        title = "Albums",
                        mediaId = ALBUMS,
                        isPlayable = false,
                        folderType = FOLDER_TYPE_ALBUMS
                    )
                )
            }
            parent == ALL_TRACKS -> {
                trackRepository.getAllTracks(
                    mediaSortPreferences = MediaSortPreferences(
                        SortOptions.TrackListSortOptions.TRACK,
                        MediaSortOrder.ASCENDING
                    )
                ).first().map { it.toMediaItemExternal() }
            }
            parent == ALBUMS -> {
                albumRepository.getAlbums(
                    MediaSortPreferences(
                        SortOptions.AlbumListSortOptions.ALBUM,
                        MediaSortOrder.ASCENDING
                    )
                ).first().map {
                    it.toMediaItemExternal()
                }
            }
            parent.startsWith("ALBUM-") -> {
                trackRepository.getTracksForAlbum(parent.substringAfterLast("-")).first()
                    .map { it.toMediaItemExternal() }
            }
            else -> {
                null
            }
        }
    }

    companion object {
        const val ROOT = "ROOT"
        const val ALL_TRACKS = "TRACKS"
        const val ALBUMS = "ALBUMS"
    }
}