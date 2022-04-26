package com.sebastianvm.musicplayer.repository.playback.mediatree

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_ALBUMS
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_ARTISTS
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_MIXED
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_NONE
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_TITLES
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.uri.UriUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class MediaTree @Inject constructor(
    @ApplicationContext private val context: Context,
    private val artistRepository: ArtistRepository,
    private val trackRepository: TrackRepository,
    private val albumRepository: AlbumRepository
) {

    private val mediaItemsTree: MutableMap<String, List<MediaItem>> = mutableMapOf()
    private val mediaItemsMap: MutableMap<String, MediaItem> = mutableMapOf()

    private fun buildMediaItem(
        title: String,
        mediaId: String,
        isPlayable: Boolean,
        @MediaMetadata.FolderType folderType: Int,
        subtitle: String? = null,
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
                .setSubtitle(subtitle)
                .setArtist(artist)
                .setGenre(genre)
                .setFolderType(folderType)
                .setIsPlayable(isPlayable)
                .setMediaUri(sourceUri)
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

    private fun Track.buildMediaItem(key: Key): MediaItem {
        return buildMediaItem(
            title = trackName,
            mediaId = key.toString(),
            isPlayable = true,
            folderType = FOLDER_TYPE_NONE,
            album = albumName,
            subtitle = artists,
            artist = artists,
            genre = "",
            sourceUri = UriUtils.getTrackUri(trackId = trackId.toLong()),
            imageUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                trackId.toLong()
            )
        )
    }

    private fun Album.buildMediaItem(): MediaItem {
        return buildMediaItem(
            title = albumName,
            mediaId = Key(KeyType.ALBUM, albumId.toLong()).toString(),
            isPlayable = false,
            folderType = FOLDER_TYPE_TITLES,
            subtitle = artists,
            album = albumName,
            artist = artists,
            genre = null,
            sourceUri = UriUtils.getAlbumUri(albumId = albumId.toLong()),
            imageUri = UriUtils.getAlbumUri(albumId = albumId.toLong())
        )
    }

    private fun Artist.buildMediaItem(): MediaItem {
        return buildMediaItem(
            title = artistName,
            mediaId = Key(KeyType.ARTIST, artistId).toString(),
            isPlayable = false,
            folderType = FOLDER_TYPE_ALBUMS,
            subtitle = null,
            album = null,
            artist = artistName,
            genre = null,
            sourceUri = null,
            imageUri = null
        )
    }

    fun getRoot(): MediaItem {
        return buildMediaItem(
            title = "Root folder",
            mediaId = Key(KeyType.ROOT, 0).toString(),
            isPlayable = false,
            folderType = FOLDER_TYPE_MIXED
        )
    }

    fun getCachedChildren(parent: String): List<MediaItem>? = mediaItemsTree[parent]
    fun getCachedMediaItem(mediaId: String): MediaItem? = mediaItemsMap[mediaId]

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getChildren(parent: String): List<MediaItem>? {
        val key = Key.fromString(parent)
        val mediaItems = when (key.type) {
            KeyType.UNKNOWN -> null
            KeyType.ROOT -> listOf(
                buildMediaItem(
                    title = "All tracks",
                    mediaId = Key(KeyType.ALL_TRACKS, 0).toString(),
                    isPlayable = false,
                    folderType = FOLDER_TYPE_TITLES
                ),
                buildMediaItem(
                    title = "Albums",
                    mediaId = Key(KeyType.ALBUMS_ROOT, 0).toString(),
                    isPlayable = false,
                    folderType = FOLDER_TYPE_ALBUMS
                ),
                buildMediaItem(
                    title = "Artists",
                    mediaId = Key(KeyType.ARTISTS_ROOT, 0).toString(),
                    isPlayable = false,
                    folderType = FOLDER_TYPE_ARTISTS
                )
            )
            KeyType.ALL_TRACKS -> {
                trackRepository.getAllTracks(
                    mediaSortPreferences = MediaSortPreferences(
                        SortOptions.TrackListSortOptions.TRACK,
                        MediaSortOrder.ASCENDING
                    )
                ).first().mapIndexed { index, track ->
                    track.buildMediaItem(
                        key.copy(index = index)
                    )
                }
            }
            KeyType.ALBUMS_ROOT -> {
                albumRepository.getAlbums(
                    MediaSortPreferences(
                        SortOptions.AlbumListSortOptions.ALBUM,
                        MediaSortOrder.ASCENDING
                    )
                ).first().map {
                    it.buildMediaItem()
                }
            }
            KeyType.ARTISTS_ROOT -> {
                artistRepository.getArtists(MediaSortOrder.ASCENDING).first()
                    .map { it.buildMediaItem() }
            }
            KeyType.GENRES_ROOT -> null
            KeyType.PLAYLISTS_ROOT -> null
            KeyType.ALBUM -> {
                trackRepository.getTracksForAlbum(key.id.toString()).first()
                    .mapIndexed { index, track ->
                        track.buildMediaItem(
                            key.copy(index = index)
                        )
                    }
            }
            KeyType.ARTIST -> {
                artistRepository.getArtist(key.id).flatMapLatest {
                    albumRepository.getAlbums(it.artistAlbums)
                }.first().map { it.buildMediaItem() }
            }
            KeyType.GENRE -> null
            KeyType.PLAYLIST -> null
            KeyType.TRACK -> null
        }
        return mediaItems?.also {
            mediaItemsTree[parent] = it
        }
    }

    suspend fun getItem(mediaId: String): MediaItem? {
        val key = Key.fromString(mediaId)
        val mediaItem = when (key.type) {
            KeyType.TRACK -> {
                trackRepository.getTrack(mediaId).first().track.buildMediaItem(
                    Key(
                        KeyType.TRACK,
                        mediaId.toLong(),
                        -1
                    )
                )
            }
            else -> null
        }
        return mediaItem?.also {
            mediaItemsMap[mediaId] = it
        }
    }

    enum class KeyType {
        UNKNOWN,
        ROOT,
        ALL_TRACKS,
        ALBUMS_ROOT,
        ARTISTS_ROOT,
        GENRES_ROOT,
        PLAYLISTS_ROOT,
        ALBUM,
        ARTIST,
        GENRE,
        PLAYLIST,
        TRACK
    }
}